(() => {

    // =========================================================
    // MULTILINGUAL (i18n) AUTO-LOADER
    // =========================================================
    if (!window.TravelBuddyTranslations) {
        const sTrans = document.createElement('script');
        sTrans.src = '../translations.js';
        document.head.appendChild(sTrans);
    }
    if (!window.TravelBuddyI18n) {
        const sI18n = document.createElement('script');
        sI18n.src = '../i18n.js';
        document.head.appendChild(sI18n);
    }

    // =========================================================
    // ROUTES
    // =========================================================

    const routes = {

        home: '../smarttour_home_2/code.html',

        explore: '../explore_destinations/explore_destinations.html',

        destinations: '../explore_destinations/explore_destinations.html',

        // IMPORTANT:
        // code.html was renamed to plan_my_trip.html
        'plan-trip': '../plan_my_trip/code.html',

        'smart-routing': '../smart_route_optimization_smarttour/code.html',

        'real-time-info': '../real_time_travel_info_smarttour/code.html',

        community: '../community_feed/code.html',

        'ai-assistant': '../ai_assistant/code.html',

        login: '../login_smarttour/code.html',

        register: '../register_smarttour/code.html',

        'destination-details':
            '../kyoto_destination_details/kyoto_destination_details.html',

        'ai-recommendations': '../ai_recommendations/code.html',

        'hidden-destination': '../hidden_destination/code.html',

        'hidden-destinations': '../hidden_destination/code.html',

        'budget-planner': '../plan_my_trip/code.html',

        'map-navigation': '../smart_route_optimization_smarttour/code.html'
    };


    // =========================================================
    // NAVIGATION FUNCTION
    // =========================================================

    const goTo = (route) => {

        if (routes[route]) {

            window.location.assign(routes[route]);

        }

    };


    // =========================================================
    // DATA-PATH LINKS
    // =========================================================

    document
        .querySelectorAll('[data-path]')
        .forEach((element) => {

            const route =
                element.dataset.path;


            if (!routes[route]) {
                return;
            }


            if (element.tagName === 'A') {

                element.href =
                    routes[route];

                return;

            }


            element.addEventListener(
                'click',
                () => goTo(route)
            );

        });


    // =========================================================
    // PROFILE IMAGE
    // =========================================================

    document
        .querySelectorAll('img[alt="Profile"]')
        .forEach((profile) => {

            const profileLink =
                profile.closest('a');

            if (profileLink) {
                profileLink.href =
                    routes.home;
                return;
            }

            profile.setAttribute(
                'aria-label',
                'TravelBuddy Profile'
            );
        });


    // =========================================================
    // BUTTON ROUTES
    // =========================================================

    /*
       IMPORTANT FIX

       DO NOT add:

       Generate: 'plan-trip'

       because the Generate Itinerary button
       must run generateItinerary()
       instead of redirecting to Plan My Trip.
    */

    const buttonRoutes = {

        'Plan My Trip': 'plan-trip',

        'Explore Destinations': 'explore',

        'Start New Chat': 'ai-assistant',

        'View Itinerary': 'plan-trip',

        'View Details': 'destination-details',

        'AI Recommendations': 'ai-recommendations',

        'Hidden Destinations': 'hidden-destinations',

        'Budget Planner': 'budget-planner',

        'Smart Routes': 'smart-routing'

    };


    document
        .querySelectorAll('button')
        .forEach((button) => {

            const label =
                button.textContent
                    .replace(/\s+/g, ' ')
                    .trim();


            /*
               IMPORTANT FIX

               Ignore the Generate Itinerary button.

               Its onclick="generateItinerary()"
               must be allowed to run normally.
            */

            if (
                label === 'Generate Itinerary' ||
                label.includes('Generate Itinerary')
            ) {

                return;

            }


            const route =
                Object
                    .entries(buttonRoutes)
                    .find(
                        ([text]) =>
                            label === text ||
                            label.includes(text)
                    )?.[1];


            if (!route) {
                return;
            }


            button.addEventListener(
                'click',
                () => goTo(route)
            );

        });


    // =========================================================
    // FEATURE ROUTES
    // =========================================================

    const featureRoutes = {

        'AI Trip Planner': 'plan-trip',

        'Smart Routing': 'smart-routing',

        'Smart Routes': 'smart-routing',

        'AI Tourist Assistant': 'ai-assistant',

        'Real-Time Info': 'real-time-info',

        'Live Travel Info': 'real-time-info',

        'Community Tourism': 'community',

        'Travel Community': 'community',

        'Personalized Recs': 'ai-recommendations',

        'AI Recommendations': 'ai-recommendations',

        'Hidden Destinations': 'hidden-destinations',

        'Budget Planning': 'budget-planner',

        'Budget Planner': 'budget-planner'

    };


    document
        .querySelectorAll('h3')
        .forEach((heading) => {

            const route =
                featureRoutes[
                    heading.textContent.trim()
                ];


            const card =
                heading.closest('.group');


            if (!route || !card) {
                return;
            }


            card.classList.add(
                'cursor-pointer'
            );


            card.tabIndex = 0;


            card.setAttribute(
                'role',
                'link'
            );


            card.setAttribute(
                'aria-label',
                `Open ${heading.textContent.trim()}`
            );


            card.addEventListener(
                'click',
                () => goTo(route)
            );


            card.addEventListener(
                'keydown',
                (event) => {

                    if (
                        event.key === 'Enter' ||
                        event.key === ' '
                    ) {

                        event.preventDefault();

                        goTo(route);

                    }

                }
            );

        });


    // =========================================================
    // SIGN UP
    // =========================================================

    const signUp =
        [
            ...document.querySelectorAll('a')
        ]
        .find(
            (link) =>
                link.textContent.trim() === 'Sign up'
        );


    if (signUp) {

        signUp.href =
            routes.register;

    }


    // =========================================================
    // LOGIN
    // =========================================================

    const signIn =
        [
            ...document.querySelectorAll('a')
        ]
        .find(
            (link) =>
                link.textContent.trim() === 'Login'
        );


    if (
        signIn &&
        signIn.getAttribute('href') === '#'
    ) {

        signIn.href =
            routes.login;

    }


    // =========================================================
    // BACK TO HOME BUTTON
    // =========================================================

    if (
        !document.querySelector(
            '[data-path="home"]'
        )
    ) {

        const homeLink =
            document.createElement('a');


        homeLink.href =
            routes.home;


        homeLink.className =
            'fixed top-5 left-5 z-50 inline-flex items-center gap-2 rounded-lg bg-surface-white/90 px-4 py-2 font-label-md text-label-md text-on-surface shadow-lg backdrop-blur hover:bg-surface-white transition-colors';


        homeLink.innerHTML =
            '<span class="material-symbols-outlined text-[18px]">arrow_back</span>Back to Home';


        document.body.append(
            homeLink
        );

    }


    // =========================================================
    // FEATURE DIRECTORY
    // =========================================================

    const featuresHeading =
        [
            ...document.querySelectorAll('h2')
        ]
        .find(
            (heading) =>
                heading.textContent.includes(
                    'Everything You Need'
                )
        );


    if (
        !featuresHeading ||
        document.getElementById(
            'feature-directory'
        )
    ) {

        return;

    }


    const extraFeatures = [

        [
            'destination-details',
            'Destination Details',
            'location_city',
            'Dive deeper into a destination before adding it to your trip.'
        ],

        [
            'real-time-info',
            'Live Travel Info',
            'update',
            'Check live weather, forecasts, and important travel alerts.'
        ],

        [
            'login',
            'Sign In',
            'login',
            'Access your TravelBuddy account and saved journeys.'
        ],

        [
            'register',
            'Create Account',
            'person_add',
            'Create an account to save and personalize your plans.'
        ]

    ];


    const featureDirectory =
        document.createElement('section');


    featureDirectory.id =
        'feature-directory';


    featureDirectory.className =
        'w-full max-w-[1280px] mx-auto px-margin-mobile lg:px-margin-desktop pb-16 lg:pb-24';


    featureDirectory.innerHTML = `

        <div class="text-center max-w-2xl mx-auto mb-10">

            <h2 class="font-headline-lg text-headline-lg text-on-surface mb-4">

                More TravelBuddy Features

            </h2>


            <p class="font-body-lg text-body-lg text-on-surface-variant">

                Everything in one place, ready when your journey needs it.

            </p>

        </div>


        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

            ${extraFeatures
                .map(
                    ([
                        route,
                        title,
                        icon,
                        description
                    ]) => `

                        <a
                            href="${routes[route]}"
                            class="bg-surface-container rounded-2xl p-6 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 group"
                        >

                            <div
                                class="w-12 h-12 rounded-full bg-secondary/10 text-secondary flex items-center justify-center mb-5 group-hover:scale-110 transition-transform"
                            >

                                <span
                                    class="material-symbols-outlined text-[24px]"
                                >
                                    ${icon}
                                </span>

                            </div>


                            <h3
                                class="font-headline-md text-headline-md text-on-surface mb-2"
                            >
                                ${title}
                            </h3>


                            <p
                                class="font-body-md text-body-md text-on-surface-variant"
                            >
                                ${description}
                            </p>

                        </a>

                    `
                )
                .join('')}

        </div>

    `;


    featuresHeading
        .closest('section')
        ?.insertAdjacentElement(
            'afterend',
            featureDirectory
        );

    window.travelBuddyLogout = window.smartTourLogout = function() {
        localStorage.removeItem('smarttour_token');
        localStorage.removeItem('smarttour_user');
        localStorage.removeItem('travelbuddy_token');
        localStorage.removeItem('travelbuddy_user');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.assign(routes.login || '../login_smarttour/code.html');
    };

})();