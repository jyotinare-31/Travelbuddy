(() => {
  const dashboard = document.querySelector('main > div');
  if (!dashboard) return;

  dashboard.innerHTML = `
    <section class="flex flex-col gap-6 mt-12">
      <div class="flex flex-col gap-2 max-w-3xl">
        <p class="font-label-md text-label-md text-secondary uppercase tracking-widest">Live weather</p>
        <h1 class="font-headline-xl text-headline-xl text-primary-container tracking-tight">Weather by city</h1>
        <p class="font-body-lg text-body-lg text-on-surface-variant">Enter a city to see current conditions and a five-day forecast for planning your trip.</p>
      </div>
      <form id="weather-search-form" class="flex flex-col sm:flex-row gap-3 max-w-3xl" novalidate>
        <label class="sr-only" for="weather-city-input">City</label>
        <div class="flex-1 relative">
          <span class="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-text-muted">location_on</span>
          <input id="weather-city-input" type="search" required minlength="2" maxlength="100" autocomplete="off" placeholder="Enter a city, e.g. Solapur or London,GB" class="w-full bg-surface-white rounded-xl py-4 pl-12 pr-4 shadow-sm border border-outline-variant/30 text-on-surface focus:outline-none focus:ring-2 focus:ring-secondary/30"/>
        </div>
        <button id="weather-search-button" type="submit" class="bg-secondary text-on-secondary px-7 py-4 rounded-xl font-label-md shadow-sm hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-70 transition-colors">Get weather</button>
      </form>
      <p id="weather-status" class="min-h-6 font-label-md text-label-md text-text-muted" aria-live="polite">Search for a city to get started.</p>
    </section>

    <section id="weather-empty-state" class="bg-surface-container-lowest rounded-[24px] shadow-sm p-8 lg:p-12 text-center max-w-3xl">
      <div class="w-16 h-16 mx-auto rounded-full bg-secondary/10 text-secondary flex items-center justify-center">
        <span class="material-symbols-outlined text-[32px]">travel_explore</span>
      </div>
      <h2 class="font-headline-md text-headline-md text-primary-container mt-5">Plan around the weather</h2>
      <p class="font-body-md text-body-md text-on-surface-variant mt-3">Search any city to see temperature, conditions, humidity, wind, pressure, visibility, and a five-day forecast.</p>
    </section>

    <section id="weather-results" class="hidden flex-col gap-gutter">
      <div class="relative bg-surface-container-lowest shadow-md rounded-[24px] p-6 lg:p-10 overflow-hidden flex flex-col gap-8">
        <div class="absolute -right-20 -top-20 w-64 h-64 bg-secondary/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="absolute -left-20 -bottom-20 w-64 h-64 bg-primary-fixed/20 rounded-full blur-3xl pointer-events-none"></div>
        <div class="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
          <div class="flex items-center gap-6">
            <div class="w-20 h-20 bg-secondary/10 rounded-full flex items-center justify-center shadow-inner">
              <span id="weather-icon" class="material-symbols-outlined text-[48px] text-secondary" style="font-variation-settings: 'FILL' 1;">light_mode</span>
            </div>
            <div class="flex flex-col">
              <span id="weather-temperature" class="font-headline-xl text-[64px] leading-none text-primary-container font-bold tracking-tighter">--°C</span>
              <span id="weather-condition" class="font-label-md text-label-md text-on-surface-variant uppercase tracking-wider mt-2">--</span>
              <span id="weather-location" class="font-label-sm text-label-sm text-text-muted mt-1">--</span>
            </div>
          </div>
          <button id="weather-refresh" type="button" class="flex items-center gap-2 bg-surface-container-low hover:bg-surface-container transition-colors rounded-full px-5 py-3 font-label-md text-on-surface">
            <span class="material-symbols-outlined">refresh</span> Refresh
          </button>
        </div>
        <div class="relative z-10 grid grid-cols-2 md:grid-cols-5 gap-3">
          <div class="bg-surface-container-low p-4 rounded-xl"><span class="font-label-sm text-label-sm text-on-surface-variant">Feels like</span><p id="weather-feels-like" class="font-headline-md text-headline-md text-primary-container mt-1">--</p></div>
          <div class="bg-surface-container-low p-4 rounded-xl"><span class="font-label-sm text-label-sm text-on-surface-variant">Humidity</span><p id="weather-humidity" class="font-headline-md text-headline-md text-primary-container mt-1">--</p></div>
          <div class="bg-surface-container-low p-4 rounded-xl"><span class="font-label-sm text-label-sm text-on-surface-variant">Wind</span><p id="weather-wind" class="font-headline-md text-headline-md text-primary-container mt-1">--</p></div>
          <div class="bg-surface-container-low p-4 rounded-xl"><span class="font-label-sm text-label-sm text-on-surface-variant">Pressure</span><p id="weather-pressure" class="font-headline-md text-headline-md text-primary-container mt-1">--</p></div>
          <div class="bg-surface-container-low p-4 rounded-xl"><span class="font-label-sm text-label-sm text-on-surface-variant">Visibility</span><p id="weather-visibility" class="font-headline-md text-headline-md text-primary-container mt-1">--</p></div>
        </div>
      </div>

      <div class="bg-surface-container-lowest shadow-md rounded-[24px] p-6 lg:p-8">
        <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-2 mb-6">
          <div>
            <p class="font-label-sm text-label-sm text-secondary uppercase tracking-widest">Forecast</p>
            <h2 class="font-headline-md text-headline-md text-primary-container mt-1">Next five days</h2>
          </div>
          <p id="weather-updated-at" class="font-label-sm text-label-sm text-text-muted"></p>
        </div>
        <div id="weather-forecast" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4"></div>
      </div>
    </section>`;

  const form = document.getElementById('weather-search-form');
  const cityInput = document.getElementById('weather-city-input');
  const submitButton = document.getElementById('weather-search-button');
  const refreshButton = document.getElementById('weather-refresh');
  const status = document.getElementById('weather-status');
  const results = document.getElementById('weather-results');
  const emptyState = document.getElementById('weather-empty-state');
  const MAX_CITY_LENGTH = 100;
  const localHosts = new Set(['localhost', '127.0.0.1', '::1']);
  const useLocalWeatherServer = window.location.protocol === 'file:'
    || (localHosts.has(window.location.hostname) && window.location.port && window.location.port !== '3000');
  const weatherApiUrl = useLocalWeatherServer ? 'http://localhost:3000/api/weather' : '/api/weather';
  let lastCity = '';
  let activeRequest;
  let requestVersion = 0;

  const escapeHtml = (value) => String(value ?? '').replace(/[&<>'"]/g, (character) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
  }[character]));
  const celsius = (value) => Number.isFinite(value) ? `${Math.round(value)}°C` : '—';
  const dateLabel = (date) => new Intl.DateTimeFormat(undefined, { weekday: 'short', month: 'short', day: 'numeric' }).format(new Date(`${date}T12:00:00`));

  function setLoading(loading) {
    submitButton.disabled = loading;
    refreshButton.disabled = loading;
    submitButton.textContent = loading ? 'Loading…' : 'Get weather';
  }

  function renderWeather(data) {
    if (!data?.current || !data?.location || !Array.isArray(data.forecast)) {
      throw new Error('The weather service returned incomplete data. Please try again.');
    }
    const { current, location, forecast, updatedAt } = data;
    document.getElementById('weather-icon').textContent = current.icon;
    document.getElementById('weather-temperature').textContent = celsius(current.temperature);
    document.getElementById('weather-condition').textContent = current.description;
    document.getElementById('weather-location').textContent = [location.city, location.country].filter(Boolean).join(', ');
    document.getElementById('weather-feels-like').textContent = celsius(current.feelsLike);
    document.getElementById('weather-humidity').textContent = Number.isFinite(current.humidity) ? `${current.humidity}%` : '—';
    document.getElementById('weather-wind').textContent = Number.isFinite(current.windKph) ? `${current.windKph} km/h` : '—';
    document.getElementById('weather-pressure').textContent = Number.isFinite(current.pressure) ? `${current.pressure} hPa` : '—';
    document.getElementById('weather-visibility').textContent = Number.isFinite(current.visibilityKm) ? `${current.visibilityKm} km` : '—';
    document.getElementById('weather-updated-at').textContent = `Updated ${new Intl.DateTimeFormat(undefined, { hour: 'numeric', minute: '2-digit' }).format(new Date(updatedAt))}`;
    document.getElementById('weather-forecast').innerHTML = forecast.map((day) => `
      <article class="bg-surface-container-low rounded-2xl p-4 flex flex-col gap-3">
        <span class="font-label-md text-label-md text-on-surface-variant">${escapeHtml(dateLabel(day.date))}</span>
        <span class="material-symbols-outlined text-secondary text-[28px]" style="font-variation-settings: 'FILL' 1;">${escapeHtml(day.icon)}</span>
        <span class="font-label-sm text-label-sm text-on-surface-variant capitalize">${escapeHtml(day.description)}</span>
        <span class="font-headline-md text-headline-md text-primary-container">${escapeHtml(celsius(day.max))}<span class="font-label-sm text-label-sm text-on-surface-variant ml-2">${escapeHtml(celsius(day.min))}</span></span>
      </article>`).join('');
    emptyState.classList.add('hidden');
    results.classList.remove('hidden');
    results.classList.add('flex');
  }

  async function loadWeather(city) {
    const normalizedCity = city.trim().replace(/\s+/g, ' ');
    if (normalizedCity.length < 2) {
      status.textContent = 'Please enter a city name with at least two characters.';
      cityInput.focus();
      return;
    }
    if (normalizedCity.length > MAX_CITY_LENGTH) {
      status.textContent = `Please enter a city name with no more than ${MAX_CITY_LENGTH} characters.`;
      cityInput.focus();
      return;
    }

    activeRequest?.abort();
    activeRequest = new AbortController();
    const thisRequest = ++requestVersion;
    setLoading(true);
    status.textContent = `Loading weather for ${normalizedCity}…`;
    try {
      const response = await fetch(`${weatherApiUrl}?city=${encodeURIComponent(normalizedCity)}`, { signal: activeRequest.signal });
      const payload = await response.json().catch(() => ({}));
      if (!response.ok) throw new Error(payload.error || 'Unable to load weather right now.');
      if (thisRequest !== requestVersion) return;
      lastCity = normalizedCity;
      cityInput.value = normalizedCity;
      renderWeather(payload);
      status.textContent = `Showing live weather for ${payload.location.city}.`;
    } catch (error) {
      if (error.name === 'AbortError') return;
      if (thisRequest !== requestVersion) return;
      status.textContent = error instanceof TypeError
        ? 'Weather service is unavailable. Run "npm start" and reload this page.'
        : error.message || 'Unable to load weather right now.';
    } finally {
      if (thisRequest === requestVersion) setLoading(false);
    }
  }

  form.addEventListener('submit', (event) => {
    event.preventDefault();
    loadWeather(cityInput.value);
  });
  refreshButton.addEventListener('click', () => {
    if (lastCity) loadWeather(lastCity);
  });
})();
