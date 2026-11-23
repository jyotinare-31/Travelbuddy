/**
 * TravelBuddy Internationalization (i18n) Engine
 * Provides instant in-place language switching, auto-mounting language selector,
 * persistent storage, and automatic dynamic DOM translation.
 */
(function() {
    'use strict';

    // Prevent duplicate initialization
    if (window.TravelBuddyI18n) return;

    const STORAGE_KEY = 'travelbuddy_lang';
    const SUPPORTED_LANGS = {
        en: { code: 'en', name: 'English', native: 'English', flag: '🌐' },
        mr: { code: 'mr', name: 'Marathi', native: 'मराठी', flag: '🌐' },
        hi: { code: 'hi', name: 'Hindi', native: 'हिंदी', flag: '🌐' }
    };

    let currentLang = localStorage.getItem(STORAGE_KEY) || 'en';
    if (!SUPPORTED_LANGS[currentLang]) {
        currentLang = 'en';
    }

    // WeakMap to store original English text nodes & attributes without mutating data
    const origTextMap = new WeakMap();
    const origPlaceholderMap = new WeakMap();
    const origTitleMap = new WeakMap();
    const origValueMap = new WeakMap();

    // Ensure Devanagari fonts are loaded for Marathi & Hindi
    function injectDevanagariFonts() {
        if (!document.getElementById('travelbuddy-devanagari-fonts')) {
            const link = document.createElement('link');
            link.id = 'travelbuddy-devanagari-fonts';
            link.rel = 'stylesheet';
            link.href = 'https://fonts.googleapis.com/css2?family=Noto+Sans+Devanagari:wght@400;500;600;700&display=swap';
            document.head.appendChild(link);

            const style = document.createElement('style');
            style.id = 'travelbuddy-i18n-styles';
            style.textContent = `
                html[lang="mr"] body, html[lang="hi"] body {
                    font-family: 'Inter', 'Noto Sans Devanagari', -apple-system, BlinkMacSystemFont, sans-serif;
                }
                .tb-lang-dropdown {
                    animation: tbDropdownFade 0.18s cubic-bezier(0.16, 1, 0.3, 1) forwards;
                }
                @keyframes tbDropdownFade {
                    from { opacity: 0; transform: translateY(-4px) scale(0.97); }
                    to { opacity: 1; transform: translateY(0) scale(1); }
                }
            `;
            document.head.appendChild(style);
        }
    }

    // Helper: Normalize phrase for dictionary lookup
    function normalizePhrase(text) {
        if (!text) return '';
        return text.replace(/\s+/g, ' ').trim().toLowerCase();
    }

    // Lookup phrase translation in dictionary
    function translatePhrase(phrase, lang) {
        if (!phrase || lang === 'en') return phrase;

        const dict = window.TravelBuddyTranslations && window.TravelBuddyTranslations.phrases;
        if (!dict) return phrase;

        const key = normalizePhrase(phrase);
        if (dict[key] && dict[key][lang]) {
            return dict[key][lang];
        }

        // Try without trailing punctuation (..., :, ., !)
        const trimmedPunct = key.replace(/[\.\:\!\?]+$/, '');
        if (dict[trimmedPunct] && dict[trimmedPunct][lang]) {
            const trailing = key.slice(trimmedPunct.length);
            return dict[trimmedPunct][lang] + trailing;
        }

        return phrase;
    }

    // Translate a single text node
    function translateTextNode(node, lang) {
        if (!node || node.nodeType !== Node.TEXT_NODE) return;

        // Skip script, style, code, pre
        const parent = node.parentElement;
        if (!parent) return;
        const tag = parent.tagName.toLowerCase();
        if (tag === 'script' || tag === 'style' || tag === 'code' || tag === 'pre' || parent.classList.contains('material-symbols-outlined')) {
            return;
        }

        const raw = node.nodeValue;
        if (!raw || !raw.trim()) return;

        // Check if we already cached original text
        let orig = origTextMap.get(node);
        if (orig === undefined) {
            orig = raw;
            origTextMap.set(node, orig);
        }

        if (lang === 'en') {
            if (node.nodeValue !== orig) {
                node.nodeValue = orig;
            }
            return;
        }

        const trimmed = orig.trim();
        const translated = translatePhrase(trimmed, lang);

        if (translated !== trimmed) {
            // Preserve leading & trailing whitespace
            const leading = orig.match(/^\s*/)[0];
            const trailing = orig.match(/\s*$/)[0];
            node.nodeValue = leading + translated + trailing;
        }
    }

    // Translate attributes (placeholder, title, value)
    function translateAttributes(el, lang) {
        if (!el || el.nodeType !== Node.ELEMENT_NODE) return;

        // Placeholder
        if (el.placeholder) {
            let orig = origPlaceholderMap.get(el);
            if (orig === undefined) {
                orig = el.placeholder;
                origPlaceholderMap.set(el, orig);
            }
            if (lang === 'en') {
                el.placeholder = orig;
            } else {
                el.placeholder = translatePhrase(orig, lang);
            }
        }

        // Title
        if (el.title && !el.classList.contains('tb-lang-btn')) {
            let orig = origTitleMap.get(el);
            if (orig === undefined) {
                orig = el.title;
                origTitleMap.set(el, orig);
            }
            if (lang === 'en') {
                el.title = orig;
            } else {
                el.title = translatePhrase(orig, lang);
            }
        }

        // Submit/button value
        if ((el.tagName === 'INPUT' && (el.type === 'submit' || el.type === 'button')) && el.value) {
            let orig = origValueMap.get(el);
            if (orig === undefined) {
                orig = el.value;
                origValueMap.set(el, orig);
            }
            if (lang === 'en') {
                el.value = orig;
            } else {
                el.value = translatePhrase(orig, lang);
            }
        }

        // Explicit data-i18n
        if (el.dataset && el.dataset.i18n) {
            const key = el.dataset.i18n;
            const translated = translatePhrase(key, lang);
            if (translated && translated !== key) {
                el.textContent = translated;
            }
        }
    }

    // Traverse DOM and translate all elements
    function translateDOM(root, lang) {
        if (!root) return;

        // Walk attributes on root
        if (root.nodeType === Node.ELEMENT_NODE) {
            translateAttributes(root, lang);
        }

        const walker = document.createTreeWalker(
            root,
            NodeFilter.SHOW_TEXT | NodeFilter.SHOW_ELEMENT,
            {
                acceptNode: function(node) {
                    if (node.nodeType === Node.ELEMENT_NODE) {
                        const tag = node.tagName.toLowerCase();
                        if (tag === 'script' || tag === 'style' || tag === 'code' || tag === 'pre') {
                            return NodeFilter.FILTER_REJECT;
                        }
                        if (node.id === 'travelbuddy-lang-widget' || node.classList.contains('travelbuddy-lang-picker')) {
                            return NodeFilter.FILTER_REJECT;
                        }
                        return NodeFilter.FILTER_ACCEPT;
                    }
                    if (node.nodeType === Node.TEXT_NODE) {
                        if (!node.nodeValue || !node.nodeValue.trim()) {
                            return NodeFilter.FILTER_SKIP;
                        }
                        const pTag = node.parentElement ? node.parentElement.tagName.toLowerCase() : '';
                        if (pTag === 'script' || pTag === 'style' || (node.parentElement && node.parentElement.classList.contains('material-symbols-outlined'))) {
                            return NodeFilter.FILTER_SKIP;
                        }
                        return NodeFilter.FILTER_ACCEPT;
                    }
                    return NodeFilter.FILTER_SKIP;
                }
            }
        );

        let node;
        while ((node = walker.nextNode())) {
            if (node.nodeType === Node.TEXT_NODE) {
                translateTextNode(node, lang);
            } else if (node.nodeType === Node.ELEMENT_NODE) {
                translateAttributes(node, lang);
            }
        }
    }

    // Update entire document language
    function applyLanguage(lang) {
        currentLang = lang;
        document.documentElement.lang = lang;
        injectDevanagariFonts();

        // Translate entire document body
        translateDOM(document.body, lang);

        // Update selector UI labels & checkmarks
        updateSelectorUI(lang);

        // Fire custom event for any page-specific handlers
        window.dispatchEvent(new CustomEvent('travelbuddy:languageChanged', {
            detail: { lang: lang, info: SUPPORTED_LANGS[lang] }
        }));
    }

    // Set Language and persist in localStorage
    function setLanguage(lang) {
        if (!SUPPORTED_LANGS[lang]) return;
        localStorage.setItem(STORAGE_KEY, lang);
        applyLanguage(lang);
    }

    // Update language selector dropdown & button display
    function updateSelectorUI(lang) {
        const langInfo = SUPPORTED_LANGS[lang] || SUPPORTED_LANGS.en;

        document.querySelectorAll('.tb-active-lang-text').forEach(el => {
            el.textContent = langInfo.native;
        });

        document.querySelectorAll('.tb-lang-opt').forEach(opt => {
            const optLang = opt.getAttribute('data-lang');
            const check = opt.querySelector('.tb-lang-check');
            if (check) {
                if (optLang === lang) {
                    check.classList.remove('hidden');
                    opt.classList.add('text-teal-700', 'font-bold', 'bg-teal-50/70');
                } else {
                    check.classList.add('hidden');
                    opt.classList.remove('text-teal-700', 'font-bold', 'bg-teal-50/70');
                }
            }
        });
    }

    // Create Language Selector Component
    function createSelectorElement() {
        const widget = document.createElement('div');
        widget.className = 'travelbuddy-lang-picker relative inline-block text-left shrink-0 z-40';
        widget.id = 'travelbuddy-lang-widget';

        const activeInfo = SUPPORTED_LANGS[currentLang] || SUPPORTED_LANGS.en;

        widget.innerHTML = `
            <button type="button" class="tb-lang-btn flex items-center gap-1.5 px-3 py-1.5 rounded-xl border border-slate-200/90 hover:border-teal-500 bg-white/95 hover:bg-teal-50/50 text-slate-700 text-xs font-bold transition-all shadow-2xs focus:outline-none cursor-pointer" title="Change Language / भाषा बदला">
                <span class="text-sm leading-none">🌐</span>
                <span class="tb-active-lang-text font-medium">${activeInfo.native}</span>
                <span class="material-symbols-outlined text-xs text-slate-400">expand_more</span>
            </button>
            <div class="tb-lang-dropdown hidden absolute right-0 mt-1.5 w-36 rounded-2xl bg-white shadow-xl border border-slate-200/90 py-1.5 z-50">
                <button type="button" data-lang="en" class="tb-lang-opt w-full px-3.5 py-2 text-left text-xs font-semibold hover:bg-teal-50 hover:text-teal-700 flex items-center justify-between cursor-pointer transition-colors ${currentLang === 'en' ? 'text-teal-700 font-bold bg-teal-50/70' : ''}">
                    <span>English</span>
                    <span class="tb-lang-check material-symbols-outlined text-sm text-teal-600 ${currentLang === 'en' ? '' : 'hidden'}">check</span>
                </button>
                <button type="button" data-lang="mr" class="tb-lang-opt w-full px-3.5 py-2 text-left text-xs font-semibold hover:bg-teal-50 hover:text-teal-700 flex items-center justify-between cursor-pointer transition-colors ${currentLang === 'mr' ? 'text-teal-700 font-bold bg-teal-50/70' : ''}">
                    <span>मराठी</span>
                    <span class="tb-lang-check material-symbols-outlined text-sm text-teal-600 ${currentLang === 'mr' ? '' : 'hidden'}">check</span>
                </button>
                <button type="button" data-lang="hi" class="tb-lang-opt w-full px-3.5 py-2 text-left text-xs font-semibold hover:bg-teal-50 hover:text-teal-700 flex items-center justify-between cursor-pointer transition-colors ${currentLang === 'hi' ? 'text-teal-700 font-bold bg-teal-50/70' : ''}">
                    <span>हिंदी</span>
                    <span class="tb-lang-check material-symbols-outlined text-sm text-teal-600 ${currentLang === 'hi' ? '' : 'hidden'}">check</span>
                </button>
            </div>
        `;

        const btn = widget.querySelector('.tb-lang-btn');
        const dropdown = widget.querySelector('.tb-lang-dropdown');

        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            dropdown.classList.toggle('hidden');
        });

        widget.querySelectorAll('.tb-lang-opt').forEach(opt => {
            opt.addEventListener('click', function(e) {
                e.stopPropagation();
                const selected = this.getAttribute('data-lang');
                dropdown.classList.add('hidden');
                setLanguage(selected);
            });
        });

        return widget;
    }

    // Auto-Mount Language Selector into Header / Navbar
    function mountLanguageSelector() {
        if (document.getElementById('travelbuddy-lang-widget')) return;

        // 1. Look for designated container
        const customContainer = document.querySelector('.nav-lang-container, [data-lang-container]');
        if (customContainer) {
            customContainer.appendChild(createSelectorElement());
            return;
        }

        // 2. Look for navbar utility bar (profile avatar, notification icon, or header actions)
        const header = document.querySelector('header');
        if (header) {
            // Find right-side action flex container
            const rightContainer = header.querySelector('.flex.items-center.gap-4, .flex.items-center.gap-3, .flex.items-center.gap-6') ||
                                   header.querySelector('#notificationBtn')?.parentElement ||
                                   header.querySelector('img[alt*="Profile"]')?.parentElement?.parentElement;

            if (rightContainer) {
                // Insert before mobile menu button or at the start of actions
                const mobileBtn = rightContainer.querySelector('#mobileMenuBtn, button[title*="Menu"], .lg\\:hidden');
                const widget = createSelectorElement();
                if (mobileBtn) {
                    rightContainer.insertBefore(widget, mobileBtn);
                } else {
                    rightContainer.insertBefore(widget, rightContainer.firstChild);
                }
                return;
            }
        }

        // 3. Fallback: top right floating pill if no header found
        const fallbackContainer = document.createElement('div');
        fallbackContainer.className = 'fixed top-4 right-4 z-50';
        fallbackContainer.appendChild(createSelectorElement());
        document.body.appendChild(fallbackContainer);
    }

    // Close dropdown on outside click
    document.addEventListener('click', function() {
        document.querySelectorAll('.tb-lang-dropdown').forEach(d => d.classList.add('hidden'));
    });

    // MutationObserver to translate dynamically rendered content
    let observerTimeout = null;
    const observer = new MutationObserver(mutations => {
        if (currentLang === 'en') return;

        let shouldTranslate = false;
        for (const mutation of mutations) {
            if (mutation.addedNodes.length > 0) {
                for (const node of mutation.addedNodes) {
                    if (node.nodeType === Node.ELEMENT_NODE && !node.classList?.contains('travelbuddy-lang-picker')) {
                        shouldTranslate = true;
                        break;
                    }
                }
            }
            if (shouldTranslate) break;
        }

        if (shouldTranslate) {
            clearTimeout(observerTimeout);
            observerTimeout = setTimeout(() => {
                translateDOM(document.body, currentLang);
            }, 60);
        }
    });

    // Public API
    window.TravelBuddyI18n = {
        getLanguage: function() { return currentLang; },
        setLanguage: setLanguage,
        t: function(key, fallback) {
            return translatePhrase(key, currentLang) || fallback || key;
        },
        translatePage: function() {
            applyLanguage(currentLang);
        },
        translateElement: function(el) {
            translateDOM(el, currentLang);
        }
    };
    window.t = window.TravelBuddyI18n.t;
    window.getTravelBuddyLang = window.TravelBuddyI18n.getLanguage;

    // Initialize on DOM Ready
    function init() {
        injectDevanagariFonts();
        mountLanguageSelector();
        if (currentLang !== 'en') {
            applyLanguage(currentLang);
        }
        observer.observe(document.body, { childList: true, subtree: true });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();

