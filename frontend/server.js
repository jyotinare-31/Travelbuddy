const http = require('node:http');
const fs = require('node:fs/promises');
const path = require('node:path');

const ROOT = __dirname;
const REQUEST_TIMEOUT_MS = 12_000;

loadEnvironment(path.join(ROOT, '.env'));

const PORT = Number(process.env.PORT || 3000);
const WEATHER_API_KEY = process.env.OPENWEATHER_API_KEY || '';

function loadEnvironment(envPath) {
  try {
    const contents = require('node:fs').readFileSync(envPath, 'utf8');
    contents.split(/\r?\n/).forEach((line) => {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith('#')) return;

      const separator = trimmed.indexOf('=');
      if (separator < 1) return;

      const key = trimmed.slice(0, separator).trim();
      let value = trimmed.slice(separator + 1).trim();
      if ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith("'") && value.endsWith("'"))) {
        value = value.slice(1, -1);
      }
      if (process.env[key] === undefined) process.env[key] = value;
    });
  } catch (error) {
    if (error.code !== 'ENOENT') throw error;
  }
}

function sendJson(response, status, payload) {
  response.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    'Cache-Control': 'no-store',
    // Lets the weather page work when it is previewed through a separate local dev server.
    'Access-Control-Allow-Origin': '*'
  });
  response.end(JSON.stringify(payload));
}

function isOpenWeatherConfigured() {
  return Boolean(WEATHER_API_KEY) && !WEATHER_API_KEY.includes('replace_with_your');
}

function weatherIcon(condition = '') {
  const normalized = condition.toLowerCase();
  if (normalized.includes('thunder')) return 'thunderstorm';
  if (normalized.includes('rain') || normalized.includes('drizzle')) return 'rainy';
  if (normalized.includes('snow')) return 'ac_unit';
  if (normalized.includes('mist') || normalized.includes('fog') || normalized.includes('haze')) return 'foggy';
  if (normalized.includes('cloud')) return 'cloud';
  return 'light_mode';
}

function weatherFromWmoCode(code, isDay = true) {
  const weatherCode = Number(code);
  if (weatherCode === 0) return { description: 'Clear sky', icon: isDay ? 'light_mode' : 'dark_mode' };
  if (weatherCode === 1) return { description: 'Mainly clear', icon: isDay ? 'wb_sunny' : 'dark_mode' };
  if (weatherCode === 2) return { description: 'Partly cloudy', icon: isDay ? 'partly_cloudy_day' : 'partly_cloudy_night' };
  if (weatherCode === 3) return { description: 'Overcast', icon: 'cloud' };
  if ([45, 48].includes(weatherCode)) return { description: 'Foggy', icon: 'foggy' };
  if ([51, 53, 55, 56, 57].includes(weatherCode)) return { description: 'Drizzle', icon: 'rainy' };
  if ([61, 63, 65, 66, 67, 80, 81, 82].includes(weatherCode)) return { description: 'Rain', icon: 'rainy' };
  if ([71, 73, 75, 77, 85, 86].includes(weatherCode)) return { description: 'Snow', icon: 'ac_unit' };
  if ([95, 96, 99].includes(weatherCode)) return { description: 'Thunderstorm', icon: 'thunderstorm' };
  return { description: 'Weather unavailable', icon: 'cloud' };
}

function rounded(value, digits = 0) {
  if (!Number.isFinite(value)) return null;
  const multiplier = 10 ** digits;
  return Math.round(value * multiplier) / multiplier;
}

function createHttpError(message, status = 502) {
  const error = new Error(message);
  error.status = status;
  return error;
}

async function fetchJson(url) {
  const response = await fetch(url, { signal: AbortSignal.timeout(REQUEST_TIMEOUT_MS) });
  let payload;

  try {
    payload = await response.json();
  } catch {
    throw createHttpError('The weather provider returned an invalid response.');
  }

  if (!response.ok) {
    throw createHttpError(payload?.message || payload?.reason || 'The weather provider could not complete the request.', response.status);
  }

  return payload;
}

function buildOpenWeatherForecast(entries, timezoneOffset) {
  const days = new Map();

  entries.forEach((entry) => {
    const localDate = new Date((entry.dt + timezoneOffset) * 1000);
    const dateKey = localDate.toISOString().slice(0, 10);
    const localHour = localDate.getUTCHours();
    const current = days.get(dateKey);
    const candidate = {
      temperature: rounded(entry.main?.temp),
      min: rounded(entry.main?.temp_min),
      max: rounded(entry.main?.temp_max),
      description: entry.weather?.[0]?.description || 'Clear sky',
      icon: weatherIcon(entry.weather?.[0]?.main),
      distanceFromNoon: Math.abs(localHour - 12)
    };

    if (!current) {
      days.set(dateKey, candidate);
      return;
    }

    current.min = Math.min(current.min ?? candidate.min, candidate.min ?? current.min);
    current.max = Math.max(current.max ?? candidate.max, candidate.max ?? current.max);
    if (candidate.distanceFromNoon < current.distanceFromNoon) {
      candidate.min = current.min;
      candidate.max = current.max;
      days.set(dateKey, candidate);
    }
  });

  return [...days.entries()].slice(0, 5).map(([date, forecast]) => ({
    date,
    temperature: forecast.temperature,
    min: forecast.min,
    max: forecast.max,
    description: forecast.description,
    icon: forecast.icon
  }));
}

async function getOpenWeather(city) {
  const params = new URLSearchParams({ q: city, appid: WEATHER_API_KEY, units: 'metric' });
  const [current, forecast] = await Promise.all([
    fetchJson(`https://api.openweathermap.org/data/2.5/weather?${params}`),
    fetchJson(`https://api.openweathermap.org/data/2.5/forecast?${params}`)
  ]);

  const condition = current.weather?.[0]?.main || 'Clear';
  return {
    provider: 'OpenWeather',
    location: {
      city: current.name,
      country: current.sys?.country || '',
      coordinates: current.coord
    },
    current: {
      temperature: rounded(current.main?.temp),
      feelsLike: rounded(current.main?.feels_like),
      humidity: rounded(current.main?.humidity),
      pressure: rounded(current.main?.pressure),
      visibilityKm: current.visibility == null ? null : rounded(current.visibility / 1000, 1),
      windKph: current.wind?.speed == null ? null : rounded(current.wind.speed * 3.6),
      description: current.weather?.[0]?.description || 'Clear sky',
      icon: weatherIcon(condition),
      observedAt: current.dt ? new Date(current.dt * 1000).toISOString() : null
    },
    forecast: buildOpenWeatherForecast(forecast.list || [], forecast.city?.timezone ?? current.timezone ?? 0),
    updatedAt: new Date().toISOString()
  };
}

function visibilityAtCurrentTime(hourly, currentTime) {
  const index = hourly?.time?.indexOf(currentTime);
  return index === -1 || index == null ? null : hourly.visibility?.[index] ?? null;
}

async function getOpenMeteoWeather(city) {
  const geocodingParams = new URLSearchParams({ name: city, count: '1', language: 'en', format: 'json' });
  const geocoding = await fetchJson(`https://geocoding-api.open-meteo.com/v1/search?${geocodingParams}`);
  const place = geocoding.results?.[0];
  if (!place) throw createHttpError('Unable to find weather for this city.', 404);

  const forecastParams = new URLSearchParams({
    latitude: String(place.latitude),
    longitude: String(place.longitude),
    current: 'temperature_2m,relative_humidity_2m,apparent_temperature,pressure_msl,wind_speed_10m,weather_code,is_day',
    hourly: 'visibility',
    daily: 'weather_code,temperature_2m_max,temperature_2m_min',
    timezone: 'auto',
    forecast_days: '5',
    wind_speed_unit: 'kmh'
  });
  const weather = await fetchJson(`https://api.open-meteo.com/v1/forecast?${forecastParams}`);
  const current = weather.current || {};
  const currentWeather = weatherFromWmoCode(current.weather_code, Boolean(current.is_day));
  const daily = weather.daily || {};
  const dateCount = Math.min(5, daily.time?.length || 0);
  const visibilityMeters = visibilityAtCurrentTime(weather.hourly, current.time);

  return {
    provider: 'Open-Meteo',
    location: {
      city: place.name,
      country: place.country_code || place.country || '',
      coordinates: { lat: place.latitude, lon: place.longitude }
    },
    current: {
      temperature: rounded(current.temperature_2m),
      feelsLike: rounded(current.apparent_temperature),
      humidity: rounded(current.relative_humidity_2m),
      pressure: rounded(current.pressure_msl),
      visibilityKm: visibilityMeters == null ? null : rounded(visibilityMeters / 1000, 1),
      windKph: rounded(current.wind_speed_10m),
      description: currentWeather.description,
      icon: currentWeather.icon,
      observedAt: current.time || null
    },
    forecast: Array.from({ length: dateCount }, (_, index) => {
      const dayWeather = weatherFromWmoCode(daily.weather_code?.[index]);
      return {
        date: daily.time[index],
        min: rounded(daily.temperature_2m_min?.[index]),
        max: rounded(daily.temperature_2m_max?.[index]),
        description: dayWeather.description,
        icon: dayWeather.icon
      };
    }),
    updatedAt: new Date().toISOString()
  };
}

async function getWeather(city) {
  if (isOpenWeatherConfigured()) {
    try {
      return await getOpenWeather(city);
    } catch (error) {
      if (error.name === 'TimeoutError' || error.name === 'AbortError') throw error;
      // A key can expire or a provider can be temporarily unavailable. The free fallback keeps the feature available.
    }
  }
  return getOpenMeteoWeather(city);
}

function contentType(filePath) {
  const extension = path.extname(filePath).toLowerCase();
  return {
    '.html': 'text/html; charset=utf-8',
    '.js': 'text/javascript; charset=utf-8',
    '.css': 'text/css; charset=utf-8',
    '.json': 'application/json; charset=utf-8',
    '.webp': 'image/webp',
    '.avif': 'image/avif',
    '.jpg': 'image/jpeg',
    '.jpeg': 'image/jpeg',
    '.png': 'image/png',
    '.svg': 'image/svg+xml'
  }[extension] || 'application/octet-stream';
}

async function serveStatic(pathname, response, method) {
  let requestedPath = pathname === '/' ? '/smarttour_home_2/code.html' : pathname;

  const aliases = {
    '/community': '/community_feed/code.html',
    '/community/': '/community_feed/code.html',
    '/community_feed': '/community_feed/code.html',
    '/community_feed/': '/community_feed/code.html',
    '/home': '/smarttour_home_2/code.html',
    '/home/': '/smarttour_home_2/code.html',
    '/login': '/login_smarttour/code.html',
    '/login/': '/login_smarttour/code.html',
    '/register': '/register_smarttour/code.html',
    '/register/': '/register_smarttour/code.html',
    '/explore': '/explore_destinations/explore_destinations.html',
    '/explore/': '/explore_destinations/explore_destinations.html',
    '/plan-trip': '/plan_my_trip/code.html',
    '/plan-trip/': '/plan_my_trip/code.html',
    '/budget': '/plan_my_trip/code.html',
    '/budget/': '/plan_my_trip/code.html',
    '/budget-planner': '/plan_my_trip/code.html',
    '/budget_planner': '/plan_my_trip/code.html',
    '/budget_planner/code.html': '/plan_my_trip/code.html',
    '/ai-recommendations': '/ai_recommendations/code.html',
    '/ai-recommendations/': '/ai_recommendations/code.html',
    '/ai-assistant': '/ai_assistant/code.html',
    '/ai-assistant/': '/ai_assistant/code.html',
    '/route-optimization': '/smart_route_optimization_smarttour/code.html',
    '/route-optimization/': '/smart_route_optimization_smarttour/code.html',
    '/route': '/smart_route_optimization_smarttour/code.html',
    '/weather': '/real_time_travel_info_smarttour/code.html',
    '/hotels': '/real_time_travel_info_smarttour/code.html',
    '/transportation': '/real_time_travel_info_smarttour/code.html',
    '/hidden-destinations': '/hidden_destination/code.html',
    '/hidden-destinations/': '/hidden_destination/code.html',
    '/hidden-gems': '/hidden_destination/code.html'
  };

  if (aliases[requestedPath]) {
    requestedPath = aliases[requestedPath];
  }

  let decodedPath;
  try {
    decodedPath = decodeURIComponent(requestedPath);
  } catch {
    sendJson(response, 400, { error: 'Invalid request path.' });
    return;
  }

  let resolvedPath = path.resolve(ROOT, `.${decodedPath}`);
  const rootPrefix = `${ROOT}${path.sep}`;
  if (resolvedPath !== ROOT && !resolvedPath.startsWith(rootPrefix)) {
    sendJson(response, 403, { error: 'Forbidden' });
    return;
  }

  try {
    const stat = await fs.stat(resolvedPath);
    if (stat.isDirectory()) {
      const codeHtmlPath = path.join(resolvedPath, 'code.html');
      try {
        await fs.access(codeHtmlPath);
        resolvedPath = codeHtmlPath;
      } catch {
        const indexHtmlPath = path.join(resolvedPath, 'index.html');
        await fs.access(indexHtmlPath);
        resolvedPath = indexHtmlPath;
      }
    }
  } catch (statError) {
    // If not found, attempt checking if it's a directory with code.html
    const fallbackPath = path.join(resolvedPath, 'code.html');
    try {
      await fs.access(fallbackPath);
      resolvedPath = fallbackPath;
    } catch {}
  }

  try {
    const data = await fs.readFile(resolvedPath);
    response.writeHead(200, { 'Content-Type': contentType(resolvedPath) });
    response.end(method === 'HEAD' ? undefined : data);
  } catch (error) {
    if (error.code === 'ENOENT') {
      sendJson(response, 404, { error: 'Page not found' });
      return;
    }
    sendJson(response, 500, { error: 'Unable to serve this file.' });
  }
}

const server = http.createServer(async (request, response) => {
  const url = new URL(request.url, `http://${request.headers.host || 'localhost'}`);

  if (request.method === 'GET' && url.pathname === '/api/health') {
    sendJson(response, 200, {
      status: 'ok',
      weatherApiConfigured: true,
      provider: isOpenWeatherConfigured() ? 'openweather' : 'open-meteo'
    });
    return;
  }

  if (request.method === 'GET' && url.pathname === '/api/weather') {
    const city = (url.searchParams.get('city') || '').trim().replace(/\s+/g, ' ');
    if (city.length < 2 || city.length > 100) {
      sendJson(response, 400, { error: 'Enter a city name between 2 and 100 characters.' });
      return;
    }

    try {
      sendJson(response, 200, await getWeather(city));
    } catch (error) {
      if (error.name === 'TimeoutError' || error.name === 'AbortError') {
        sendJson(response, 504, { error: 'The weather service timed out. Please try again.' });
        return;
      }
      sendJson(response, error.status === 404 ? 404 : 502, { error: error.message || 'Unable to load weather right now.' });
    }
    return;
  }

  if (request.method !== 'GET' && request.method !== 'HEAD') {
    sendJson(response, 405, { error: 'Method not allowed' });
    return;
  }

  await serveStatic(url.pathname, response, request.method);
});

server.listen(PORT, () => {
  console.log(`TravelBuddy is running at http://localhost:${PORT}`);
});
