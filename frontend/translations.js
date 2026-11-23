/**
 * TravelBuddy Multilingual Translations Dictionary
 * Supported Languages:
 *   - 'en': English (Default)
 *   - 'mr': मराठी (Marathi)
 *   - 'hi': हिंदी (Hindi)
 */
(function() {
    'use strict';

    const translations = {
        // Core Metadata
        meta: {
            languages: {
                en: { name: 'English', native: 'English', flag: '🇬🇧' },
                mr: { name: 'Marathi', native: 'मराठी', flag: '🇮🇳' },
                hi: { name: 'Hindi', native: 'हिंदी', flag: '🇮🇳' }
            }
        },

        // Dictionary of exact and phrase-level translations
        // Keyed by lowercase trimmed English phrase
        phrases: {
            // Navigation & Branding
            "travelbuddy": { mr: "TravelBuddy", hi: "TravelBuddy" },
            "home": { mr: "मुख्यपृष्ठ", hi: "होम" },
            "explore": { mr: "शोधा", hi: "खोजें" },
            "explore destinations": { mr: "पर्यटन स्थळे शोधा", hi: "पर्यटन स्थल खोजें" },
            "plan trip": { mr: "सहलीचे नियोजन", hi: "यात्रा योजना" },
            "plan my trip": { mr: "माझ्या सहलीचे नियोजन करा", hi: "मेरी यात्रा की योजना बनाएं" },
            "smart routes": { mr: "स्मार्ट मार्ग", hi: "स्मार्ट रूट" },
            "route optimization": { mr: "मार्ग ऑप्टिमायझेशन", hi: "रूट ऑप्टिमाइज़ेशन" },
            "ai recommendations": { mr: "एआय शिफारसी", hi: "एआई सिफारिशें" },
            "ai recs": { mr: "एआय शिफारसी", hi: "एआई सिफारिशें" },
            "hidden destinations": { mr: "लपलेली ठिकाणे", hi: "छिपे हुए स्थल" },
            "hidden gems": { mr: "लपलेली रत्ने", hi: "अनदेखे स्थल" },
            "community": { mr: "समुदाय", hi: "समुदाय" },
            "community feed": { mr: "कम्युनिटी फीड", hi: "कम्युनिटी फीड" },
            "ai assistant": { mr: "एआय सहाय्यक", hi: "एआई सहायक" },
            "assistant": { mr: "सहाय्यक", hi: "सहायक" },
            "budget planner": { mr: "बजेट प्लॅनर", hi: "बजट प्लानर" },
            "live weather": { mr: "थेट हवामान", hi: "लाइव मौसम" },
            "real-time info": { mr: "रिअल-टाइम माहिती", hi: "रीयल-टाइम जानकारी" },
            "live travel info": { mr: "थेट प्रवास माहिती", hi: "लाइव यात्रा जानकारी" },
            "sign in": { mr: "लॉगिन करा", hi: "लॉगिन करें" },
            "sign up": { mr: "नोंदणी करा", hi: "साइन अप करें" },
            "get started": { mr: "सुरू करा", hi: "शुरू करें" },
            "login": { mr: "लॉगिन", hi: "लॉगिन" },
            "register": { mr: "नोंदणी", hi: "रजिस्टर" },
            "logout": { mr: "लॉगआउट करा", hi: "लॉगआउट करें" },
            "profile": { mr: "प्रोफाइल", hi: "प्रोफ़ाइल" },
            "notifications": { mr: "सूचना", hi: "सूचनाएं" },
            "back to home": { mr: "मुख्यपृष्ठावर परत जा", hi: "मुख्य पृष्ठ पर लौटें" },
            "search": { mr: "शोधा", hi: "खोजें" },
            "search places...": { mr: "ठिकाणे शोधा...", hi: "स्थान खोजें..." },
            "search solapur...": { mr: "सोलापूर शोधा...", hi: "सोलापुर खोजें..." },
            "search journeys...": { mr: "प्रवास शोधा...", hi: "यात्रा खोजें..." },
            "ai tourism planner": { mr: "एआय पर्यटन नियोजक", hi: "एआई पर्यटन प्लानर" },

            // Topbar
            "welcome to travelbuddy — intelligent solapur tourism & route optimization": {
                mr: "TravelBuddy मध्ये आपले स्वागत आहे — बुद्धिमान सोलापूर पर्यटन आणि मार्ग नियोजन",
                hi: "TravelBuddy में आपका स्वागत है — स्मार्ट सोलापुर पर्यटन और रूट ऑप्टिमाइज़ेशन"
            },
            "solapur weather: loading...": { mr: "सोलापूर हवामान: लोड होत आहे...", hi: "सोलापुर मौसम: लोड हो रहा है..." },
            "ai assistant 24/7": { mr: "२४/७ एआय सहाय्यक", hi: "२४/७ एआई सहायक" },
            "rag active": { mr: "आरएजी सक्रिय", hi: "आरएजी सक्रिय" },
            "rag online": { mr: "आरएजी ऑनलाइन", hi: "आरएजी ऑनलाइन" },

            // Hero Section
            "discover solapur's heritage & plan smarter journeys": {
                mr: "सोलापूरचा समृद्ध वारसा शोधा आणि स्मार्ट सहलीचे नियोजन करा",
                hi: "सोलापुर की समृद्ध विरासत खोजें और स्मार्ट यात्रा की योजना बनाएं"
            },
            "smart tourism planner & route optimization": {
                mr: "स्मार्ट पर्यटन नियोजक आणि मार्ग ऑप्टिमायझेशन",
                hi: "स्मार्ट पर्यटन प्लानर और रूट ऑप्टिमाइज़ेशन"
            },
            "ai-powered route optimization, verified temple schedules, authentic maharashtrian cuisine, and local hidden gems — all tailored for your budget.": {
                mr: "एआय-चालित मार्ग ऑप्टिमायझेशन, मंदिरांच्या खात्रीशीर वेळा, अस्सल महाराष्ट्रीयन खाद्यपदार्थ आणि स्थानिक अप्रतिम ठिकाणे — सर्वकाही तुमच्या बजेटनुसार.",
                hi: "एआई-संचालित रूट ऑप्टिमाइज़ेशन, मंदिरों का प्रामाणिक समय, पारंपरिक महाराष्ट्रीयन भोजन और छिपे हुए पर्यटन स्थल — सब आपके बजट के अनुसार।"
            },
            "explore destinations": { mr: "पर्यटन स्थळे शोधा", hi: "पर्यटन स्थल देखें" },
            "find my journey": { mr: "माझा प्रवास शोधा", hi: "मेरी यात्रा खोजें" },
            "ai trip planner": { mr: "एआय सहल नियोजक", hi: "एआई ट्रिप प्लानर" },
            "smart route optimization": { mr: "स्मार्ट मार्ग ऑप्टिमायझेशन", hi: "स्मार्ट रूट ऑप्टिमाइज़ेशन" },
            "live budget calculator": { mr: "थेट बजेट कॅल्क्युलेटर", hi: "लाइव बजट कैलकुलेटर" },

            // Search Bar Widget
            "where are you starting from?": { mr: "प्रवास कुठून सुरू करत आहात?", hi: "आपकी यात्रा कहाँ से शुरू हो रही है?" },
            "starting location": { mr: "सुरुवातीचे ठिकाण", hi: "प्रारंभिक स्थान" },
            "total budget": { mr: "एकूण बजेट", hi: "कुल बजट" },
            "budget in inr": { mr: "रुपयांमध्ये बजेट", hi: "बजट (रुपये में)" },
            "trip duration": { mr: "सहलीचा कालावधी", hi: "यात्रा की अवधि" },
            "travel preference": { mr: "प्रवासाची पसंती", hi: "यात्रा प्राथमिकता" },
            "interests": { mr: "आवड / रुची", hi: "रुचियां" },
            "all categories": { mr: "सर्व श्रेणी", hi: "सभी श्रेणियां" },
            "preferred pace": { mr: "पसंतीची गती", hi: "पसंदीदा गति" },
            "relaxed": { mr: "शांत / निवांत", hi: "आरामदायक" },
            "balanced": { mr: "संतुलित", hi: "संतुलित" },
            "fast": { mr: "जलद", hi: "तेज़" },
            "solo": { mr: "एकटे", hi: "अकेले" },
            "couple": { mr: "जोडपे", hi: "दंपति" },
            "family": { mr: "कुटुंब", hi: "परिवार" },
            "friends": { mr: "मित्र", hi: "मित्र" },

            // Stats Banner
            "50+ verified places": { mr: "५०+ खात्रीशीर ठिकाणे", hi: "५०+ सत्यापित स्थान" },
            "4.8 traveler rating": { mr: "४.८ पर्यटक रेटिंग", hi: "४.८ यात्री रेटिंग" },
            "100% free ai planning": { mr: "१००% मोफत एआय नियोजन", hi: "१००% मुफ्त एआई प्लानिंग" },
            "weather & travel alerts": { mr: "हवामान आणि प्रवास सूचना", hi: "मौसम और यात्रा अलर्ट" },
            "solapur district, maharashtra": { mr: "सोलापूर जिल्हा, महाराष्ट्र", hi: "सोलापुर जिला, महाराष्ट्र" },

            // Popular Solapur Destinations
            "popular attractions in solapur": { mr: "सोलापूरमधील लोकप्रिय आकर्षणे", hi: "सोलापुर के प्रमुख आकर्षण" },
            "handpicked must-visit landmarks across solapur city and district": {
                mr: "सोलापूर शहर आणि जिल्ह्यातील अवश्य भेट द्यावी अशी निवडक स्थळे",
                hi: "सोलापुर शहर और जिले के अवश्य दर्शनीय स्थल"
            },
            "siddheshwar temple": { mr: "सिद्धेशवर मंदिर", hi: "सिद्धेश्वर मंदिर" },
            "pandharpur vitthal temple": { mr: "पंढरपूर विठ्ठल मंदिर", hi: "पंढरपुर विट्ठल मंदिर" },
            "akkalkot swami samarth temple": { mr: "अक्कलकोट स्वामी समर्थ मंदिर", hi: "अक्कलकोट स्वामी समर्थ मंदिर" },
            "great indian bustard sanctuary": { mr: "माळढोक पक्षी अभयारण्य (नान्नज)", hi: "ग्रेट इंडियन बस्टर्ड अभयारण्य" },
            "solapur bhuikot fort": { mr: "सोलापूर भुईकोट किल्ला", hi: "सोलापुर भुईकोट किला" },
            "hippargi lake": { mr: "हिप्परगी तलाव", hi: "हिप्परगी झील" },
            "naldurg fort": { mr: "नलदुर्ग किल्ला", hi: "नलदुर्ग किला" },
            "karmala": { mr: "करमाळा", hi: "करमाला" },
            "kudal sangam": { mr: "कुडल संगम", hi: "कुडल संगम" },

            // Delicacies & Specialities
            "famous solapuri foods": { mr: "प्रसिद्ध सोलापूरी खाद्यपदार्थ", hi: "प्रसिद्ध सोलापुरी व्यंजन" },
            "solapuri shenga chutney": { mr: "सोलापूरी शेंगदाणा चटणी", hi: "सोलापुरी मूंगफली चटनी" },
            "solapuri jowar bhakri": { mr: "सोलापूरी ज्वारीची भाकरी", hi: "सोलापुरी ज्वार की भाकरी" },
            "solapuri chaddars and towels": { mr: "सोलापूरी चादर आणि टॉवेल्स", hi: "सोलापुरी चादर और तौलिए" },
            "textile capital of maharashtra": { mr: "महाराष्ट्राची वस्त्रनगरी", hi: "महाराष्ट्र की कपड़ा राजधानी" },
            "spiritual capital": { mr: "आध्यात्मिक राजधानी", hi: "आध्यात्मिक राजधानी" },

            // Live Weather Section
            "real-time solapur climate": { mr: "थेट सोलापूर हवामान", hi: "लाइव सोलापुर मौसम" },
            "live weather forecast": { mr: "थेट हवामान अंदाज", hi: "लाइव मौसम पूर्वानुमान" },
            "temperature": { mr: "तापमान", hi: "तापमान" },
            "humidity": { mr: "आर्द्रता", hi: "आर्द्रता" },
            "wind": { mr: "वारा", hi: "हवा" },
            "wind speed": { mr: "वाऱ्याचा वेग", hi: "हवा की गति" },
            "weather condition": { mr: "हवामानाची स्थिती", hi: "मौसम की स्थिति" },
            "5-day forecast": { mr: "५ दिवसांचा अंदाज", hi: "५ दिन का पूर्वानुमान" },
            "today": { mr: "आज", hi: "आज" },
            "mon": { mr: "सोम", hi: "सोम" },
            "tue": { mr: "मंगळ", hi: "मंगल" },
            "wed": { mr: "बुध", hi: "बुध" },
            "thu": { mr: "गुरु", hi: "गुरु" },
            "fri": { mr: "शुक्र", hi: "शुक्र" },
            "sat": { mr: "शनि", hi: "शनि" },
            "sun": { mr: "रवि", hi: "रवि" },

            // Bento Features
            "engineered for effortless exploration": {
                mr: "सहज आणि आनंददायी प्रवासासाठी डिझाइन केलेले",
                hi: "सहज और सुखद यात्रा के लिए विशेष रूप से निर्मित"
            },
            "end-to-end travel suite": { mr: "संपूर्ण प्रवास सुविधा संच", hi: "संपूर्ण यात्रा सुविधा सुइट" },
            "plan my itinerary": { mr: "माझे नियोजन तयार करा", hi: "मेरी यात्रा की योजना बनाएं" },
            "open route map": { mr: "मार्ग नकाशा उघडा", hi: "रूट मैप खोलें" },
            "calculate trip cost": { mr: "सहलीच्या खर्चाची गणना करा", hi: "यात्रा खर्च की गणना करें" },
            "travelbuddy community": { mr: "TravelBuddy समुदाय", hi: "TravelBuddy समुदाय" },
            "join community feed": { mr: "कम्युनिटी फीडमध्ये सामील व्हा", hi: "कम्युनिटी फीड में शामिल हों" },
            "24/7 ai tourist assistant (rag)": { mr: "२४/७ एआय पर्यटन सहाय्यक (RAG)", hi: "२४/७ एआई पर्यटन सहायक (RAG)" },
            "start ai conversation": { mr: "एआय संवाद सुरू करा", hi: "एआई बातचीत शुरू करें" },

            // All-Modules Hub
            "complete system access": { mr: "सर्व प्रणालींमध्ये थेट प्रवेश", hi: "सभी सुविधाओं तक पहुंच" },
            "explore all travelbuddy features": { mr: "सर्व TravelBuddy वैशिष्ट्ये एक्सप्लोर करा", hi: "TravelBuddy के सभी फीचर्स देखें" },
            "direct shortcuts to all key modules across our smart tourism platform": {
                mr: "आमच्या स्मार्ट पर्यटन प्लॅटफॉर्मवरील सर्व मुख्य मॉड्यूल्सचे थेट शॉर्टकट",
                hi: "हमारे स्मार्ट पर्यटन प्लेटफॉर्म के सभी प्रमुख मॉड्यूल्स के सीधे शॉर्टकट"
            },

            // Common Actions & Form Controls
            "view details": { mr: "तपशील पहा", hi: "विवरण देखें" },
            "view on map": { mr: "नकाशावर पहा", hi: "मैप पर देखें" },
            "add to trip": { mr: "सहलीमध्ये जोडा", hi: "यात्रा में जोड़ें" },
            "add to favorites": { mr: "आवडीमध्ये जोडा", hi: "पसंदीदा में जोड़ें" },
            "remove": { mr: "काढून टाका", hi: "हटाएं" },
            "delete": { mr: "हटवा", hi: "हटाएं" },
            "reset": { mr: "रीसेट करा", hi: "रीसेट करें" },
            "save": { mr: "जतन करा", hi: "सहेजें" },
            "cancel": { mr: "रद्द करा", hi: "रद्द करें" },
            "submit": { mr: "सबमिट करा", hi: "सबमिट करें" },
            "send": { mr: "पाठवा", hi: "भेजें" },
            "apply filters": { mr: "फिल्टर लागू करा", hi: "फ़िल्टर लागू करें" },
            "clear all": { mr: "सर्व साफ करा", hi: "सभी साफ़ करें" },
            "loading...": { mr: "लोड होत आहे...", hi: "लोड हो रहा है..." },
            "search by name, locality, or category...": { mr: "नाव, परिसर किंवा श्रेणीनुसार शोधा...", hi: "नाम, इलाके या श्रेणी से खोजें..." },

            // Destination Card Badges & Metadata
            "visiting hours": { mr: "भेट देण्याची वेळ", hi: "दर्शन का समय" },
            "opening hours": { mr: "उघडण्याची वेळ", hi: "खुलने का समय" },
            "entry fee": { mr: "प्रवेश शुल्क", hi: "प्रवेश शुल्क" },
            "free entry": { mr: "मोफत प्रवेश", hi: "मुफ्त प्रवेश" },
            "free": { mr: "मोफत", hi: "मुफ्त" },
            "rating": { mr: "रेटिंग", hi: "रेटिंग" },
            "reviews": { mr: "पुनरावलोकने", hi: "समीक्षाएं" },
            "location": { mr: "स्थान", hi: "स्थान" },
            "distance": { mr: "अंतर", hi: "दूरी" },
            "category": { mr: "श्रेणी", hi: "श्रेणी" },
            "open now": { mr: "आता उघडे आहे", hi: "अभी खुला है" },
            "closed": { mr: "बंद आहे", hi: "बंद है" },
            "hidden gem": { mr: "लपलेले रत्न", hi: "अनदेखा स्थल" },
            "verified": { mr: "खात्रीशीर", hi: "सत्यापित" },

            // Category Filters
            "all": { mr: "सर्व", hi: "सभी" },
            "temple": { mr: "मंदिर", hi: "मंदिर" },
            "temples": { mr: "मंदिरे", hi: "मंदिर" },
            "fort": { mr: "किल्ला", hi: "किला" },
            "forts": { mr: "किल्ले", hi: "किले" },
            "spiritual": { mr: "आध्यात्मिक", hi: "आध्यात्मिक" },
            "history": { mr: "इतिहास", hi: "इतिहास" },
            "historical": { mr: "ऐतिहासिक", hi: "ऐतिहासिक" },
            "nature": { mr: "निसर्ग", hi: "प्रकृति" },
            "wildlife": { mr: "वन्यजीव", hi: "वन्यजीव" },
            "food": { mr: "खाद्यपदार्थ", hi: "खानपान" },
            "shopping": { mr: "खरेदी", hi: "खरीदारी" },
            "family friendly": { mr: "कुटुंबासाठी अनुकूल", hi: "परिवार के अनुकूल" },
            "agro-tourism": { mr: "कृषी पर्यटन", hi: "कृषि पर्यटन" },

            // AI Assistant Page
            "your ai travel companion": { mr: "तुमचा एआय प्रवास साथीदार", hi: "आपका एआई यात्रा साथी" },
            "live solapur district tourism assistant & interactive guidebook": {
                mr: "सोलापूर जिल्हा पर्यटन थेट सहाय्यक आणि परस्परसंवादी मार्गदर्शक",
                hi: "सोलापुर जिला पर्यटन लाइव सहायक और इंटरएक्टिव गाइडबुक"
            },
            "where would you like to explore in solapur?": {
                mr: "तुम्हाला सोलापूरमध्ये कुठे फिरायचे आहे?",
                hi: "आप सोलापुर में कहां घूमना चाहते हैं?"
            },
            "new conversation": { mr: "नवीन संवाद", hi: "नई बातचीत" },
            "reset chat": { mr: "चॅट रीसेट करा", hi: "चैट रीसेट करें" },
            "popular solapur questions": { mr: "सोलापूरचे लोकप्रिय प्रश्न", hi: "सोलापुर के लोकप्रिय प्रश्न" },
            "ask anything about solapur tourism, temples, food, routes...": {
                mr: "सोलापूर पर्यटन, मंदिरे, खाद्यपदार्थ, मार्गांविषयी काहीही विचारा...",
                hi: "सोलापुर पर्यटन, मंदिरों, भोजन, मार्गों के बारे में कुछ भी पूछें..."
            },
            "retrieved sources": { mr: "संदर्भित कागदपत्रे", hi: "संदर्भित स्रोत" },
            "knowledge base": { mr: "माहिती कोश", hi: "नॉलेज बेस" },
            "searching solapur tourism database and formulating answer...": {
                mr: "सोलापूर पर्यटन माहिती शोधून उत्तराची मांडणी करत आहे...",
                hi: "सोलापुर पर्यटन डेटाबेस खोजकर उत्तर तैयार किया जा रहा है..."
            },
            "quick ask:": { mr: "त्वरित विचारा:", hi: "त्वरित पूछें:" },
            "copy answer": { mr: "उत्तर कॉपी करा", hi: "उत्तर कॉपी करें" },

            // AI Recommendations Page
            "ai-powered tourism recommendations": { mr: "एआय-चालित पर्यटन शिफारसी", hi: "एआई-संचालित पर्यटन सिफारिशें" },
            "generate personalized picks": { mr: "वैयक्तिक शिफारसी मिळवा", hi: "व्यक्तिगत सिफारिशें प्राप्त करें" },
            "generate ai recommendations": { mr: "एआय शिफारसी जनरेट करा", hi: "एआई सिफारिशें तैयार करें" },
            "best of solapur": { mr: "सोलापूरमधील सर्वोत्तम", hi: "सोलापुर का सर्वश्रेष्ठ" },
            "district specialities": { mr: "जिल्ह्याची खास वैशिष्ट्ये", hi: "जिले की विशेषताएं" },
            "traveler interests": { mr: "पर्यटकांची आवड", hi: "यात्री की रुचियां" },
            "starting point": { mr: "सुरुवातीचे ठिकाण", hi: "प्रारंभिक स्थल" },
            "estimated ticket price": { mr: "अंदाजे तिकीट दर", hi: "अनुमानित टिकट मूल्य" },
            "why we recommend this": { mr: "आम्ही याची शिफारस का करतो", hi: "हम इसकी सिफारिश क्यों करते हैं" },

            // Route Optimization Page
            "smart route optimization for solapur district": {
                mr: "सोलापूर जिल्ह्यासाठी स्मार्ट मार्ग ऑप्टिमायझेशन",
                hi: "सोलापुर जिले के लिए स्मार्ट रूट ऑप्टिमाइज़ेशन"
            },
            "select destinations in solapur district": {
                mr: "सोलापूर जिल्ह्यातील पर्यटन स्थळे निवडा",
                hi: "सोलापुर जिले के दर्शनीय स्थल चुनें"
            },
            "choose starting point": { mr: "सुरुवातीचे ठिकाण निवडा", hi: "प्रारंभिक स्थल चुनें" },
            "choose destination": { mr: "अंतिम ठिकाण निवडा", hi: "गंतव्य स्थल चुनें" },
            "optimize route": { mr: "मार्ग ऑप्टिमाइझ करा", hi: "रूट ऑप्टिमाइज़ करें" },
            "optimized visiting order": { mr: "ऑप्टिमाइझ केलेला भेट क्रम", hi: "ऑप्टिमाइज़ किया गया यात्रा क्रम" },
            "total travel distance": { mr: "एकूण प्रवास अंतर", hi: "कुल यात्रा दूरी" },
            "total travel time": { mr: "एकूण प्रवास वेळ", hi: "कुल यात्रा समय" },
            "turn-by-turn directions": { mr: "वळणावळणाचे दिशानिर्देश", hi: "बारीक दिशानिर्देश" },
            "original route": { mr: "मूळ मार्ग", hi: "मूल मार्ग" },
            "optimized route": { mr: "ऑप्टिमाइझ केलेला मार्ग", hi: "ऑप्टिमाइज़ किया गया मार्ग" },
            "both routes": { mr: "दोन्ही मार्ग", hi: "दोनों मार्ग" },
            "provide custom route": { mr: "सानुकूल मार्ग द्या", hi: "कस्टम रूट प्रदान करें" },
            "compare original vs. optimized": { mr: "मूळ विरूद्ध ऑप्टिमाइझ तुलना", hi: "मूल बनाम ऑप्टिमाइज़्ड तुलना" },
            "route comparison & analysis": { mr: "मार्ग तुलना आणि विश्लेषण", hi: "रूट तुलना और विश्लेषण" },
            "distance difference": { mr: "अंतरातील फरक", hi: "दूरी में अंतर" },
            "time difference": { mr: "वेळेतील फरक", hi: "समय में अंतर" },

            // Trip Planner & Budget
            "build your solapur itinerary": { mr: "तुमचे सोलापूर सहल नियोजन तयार करा", hi: "अपनी सोलापुर यात्रा की योजना बनाएं" },
            "select number of days": { mr: "दिवसांची संख्या निवडा", hi: "दिनों की संख्या चुनें" },
            "morning slot": { mr: "सकाळचे सत्र", hi: "सुबह का समय" },
            "afternoon slot": { mr: "दुपारचे सत्र", hi: "दोपहर का समय" },
            "evening slot": { mr: "संध्याकाळचे सत्र", hi: "शाम का समय" },
            "estimated budget breakdown": { mr: "अंदाजे बजेट विश्लेषण", hi: "अनुमानित बजट विवरण" },
            "hotel expenses": { mr: "हॉटेल खर्च", hi: "होटल खर्च" },
            "food & dining": { mr: "अन्न आणि जेवण", hi: "भोजन और खानपान" },
            "local transportation": { mr: "स्थानिक वाहतूक", hi: "स्थानीय परिवहन" },
            "entry tickets": { mr: "प्रवेश तिकिटे", hi: "प्रवेश टिकट" },
            "total estimated cost": { mr: "एकूण अंदाजे खर्च", hi: "कुल अनुमानित लागत" },

            // Community Feed
            "share your solapur experience": { mr: "तुमचा सोलापूर अनुभव सामायिक करा", hi: "अपना सोलापुर अनुभव साझा करें" },
            "traveler stories & reviews": { mr: "पर्यटकांचे अनुभव आणि पुनरावलोकने", hi: "यात्रियों के अनुभव और समीक्षाएं" },
            "write a review": { mr: "पुनरावलोकन लिहा", hi: "समीक्षा लिखें" },
            "post title": { mr: "शीर्षक", hi: "शीर्षक" },
            "share photos and tips": { mr: "फोटो आणि टिप्स सामायिक करा", hi: "फ़ोटो और टिप्स साझा करें" },
            "likes": { mr: "लाईक्स", hi: "पसंद" },
            "comments": { mr: "टिप्पण्या", hi: "टिप्पणियां" },

            // Auth Pages
            "welcome back": { mr: "पुन्हा स्वागत आहे", hi: "वापसी पर स्वागत" },
            "sign in to travelbuddy": { mr: "TravelBuddy मध्ये लॉगिन करा", hi: "TravelBuddy में लॉगिन करें" },
            "create your account": { mr: "तुमचे खाते तयार करा", hi: "अपना खाता बनाएं" },
            "email address": { mr: "ईमेल पत्ता", hi: "ईमेल पता" },
            "password": { mr: "पासवर्ड", hi: "पासवर्ड" },
            "full name": { mr: "पूर्ण नाव", hi: "पूरा नाम" },
            "remember me": { mr: "माझी आठवण ठेवा", hi: "मुझे याद रखें" },
            "forgot password?": { mr: "पासवर्ड विसरलात?", hi: "पासवर्ड भूल गए?" },
            "don't have an account?": { mr: "खाते नाही का?", hi: "खाता नहीं है?" },
            "already have an account?": { mr: "आधीच खाते आहे का?", hi: "पहले से खाता है?" },

            // Footer
            "ai-powered smart tourism management system for solapur district": {
                mr: "सोलापूर जिल्ह्यासाठी एआय-चालित स्मार्ट पर्यटन व्यवस्थापन प्रणाली",
                hi: "सोलापुर जिले के लिए एआई-संचालित स्मार्ट पर्यटन प्रबंधन प्रणाली"
            },
            "crafting unforgettable journeys with ai-powered precision and local heart.": {
                mr: "स्थानिक अस्मितेसह आणि एआय-चालित अचूकतेने अविस्मरणीय प्रवास घडवणे.",
                hi: "स्थानीय संस्कृति और एआई-संचालित सटीकता के साथ अविस्मरणीय यात्रा अनुभव।"
            },
            "company": { mr: "कंपनी", hi: "कंपनी" },
            "about us": { mr: "आमच्याबद्दल", hi: "हमारे बारे में" },
            "careers": { mr: "करिअर", hi: "करियर" },
            "support": { mr: "मदत आणि सहाय्य", hi: "सहायता" },
            "help center": { mr: "मदत केंद्र", hi: "सहायता केंद्र" },
            "safety tips": { mr: "सुरक्षा टिप्स", hi: "सुरक्षा सुझाव" },
            "privacy policy": { mr: "गोपनीयता धोरण", hi: "गोपनीयता नीति" },
            "terms of service": { mr: "सेवा अटी", hi: "सेवा की शर्तें" },
            "all rights reserved.": { mr: "सर्व हक्क राखीव.", hi: "सर्वाधिकार सुरक्षित।" }
        }
    };

    window.TravelBuddyTranslations = translations;
})();

