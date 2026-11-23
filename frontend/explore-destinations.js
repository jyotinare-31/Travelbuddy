console.log("EXPLORE DESTINATION JS LOADED");
 
 
// =====================================================
// BACKEND API URLS
// =====================================================
 
const BASE_URL = "http://localhost:8081";
 
const API_URL = `${BASE_URL}/api/tourist-places`;
 
const HIDDEN_DESTINATIONS_API = `${BASE_URL}/api/tourist-places/hidden`;
 
// Change this later if your RAG backend uses a different endpoint.
const AI_ASSISTANT_API = `${BASE_URL}/api/ai/chat`;
 
 
// =====================================================
// GLOBAL STATE
// =====================================================
 
// AI conversation history (FIX: this was never declared)
let aiConversation = [];
 
// Destination search data (FIX: now filled in loadDestinations)
let allDestinations = [];
 
 
// =====================================================
// HELPERS
// =====================================================
 
function escapeHtml(value) {
 
    const div = document.createElement("div");
 
    div.textContent = value ?? "";
 
    return div.innerHTML;
 
}
 
 
function resolveImageUrl(imageUrl, placeholderText) {
 
    if (imageUrl && !imageUrl.startsWith("http")) {
        imageUrl = `${BASE_URL}${imageUrl}`;
    }
 
    if (!imageUrl) {
        imageUrl =
            `https://via.placeholder.com/600x400?text=${placeholderText}`;
    }
 
    return imageUrl;
 
}
 
 
// =====================================================
// LOAD DESTINATIONS FROM BACKEND
// =====================================================
 
async function loadDestinations() {
 
    try {
 
        console.log("Connecting to backend:", API_URL);
 
        const response = await fetch(API_URL);
 
        if (!response.ok) {
            throw new Error(
                `Backend returned ${response.status} ${response.statusText}`
            );
        }
 
        const destinations = await response.json();
 
        console.log("Destinations received:", destinations);
 
        if (!Array.isArray(destinations)) {
            throw new Error("Backend did not return an array.");
        }
 
        // FIX: store data so search can use it
        allDestinations = destinations;
 
        displayDestinations(destinations);
 
    } catch (error) {
 
        console.error("Error loading destinations:", error);
 
        const grid = document.getElementById("destination-grid");
 
        if (grid) {
 
            grid.innerHTML = `
 
                <div class="col-span-full text-center py-10">
 
                    <p class="text-red-500 text-lg">
                        Unable to load destinations from backend.
                    </p>
 
                    <p class="text-gray-500 mt-2">
                        Please make sure the Spring Boot backend is running.
                    </p>
 
                </div>
 
            `;
 
        }
 
    }
 
}
 
 
// =====================================================
// DESTINATION SEARCH
// =====================================================
 
function setupDestinationSearch() {
 
    const searchInput =
        document.getElementById("destination-search");
 
    const searchButton =
        document.getElementById("destination-search-button");
 
    if (!searchInput) {
        console.error("destination-search input not found.");
        return;
    }
 
    function performSearch() {
 
        const searchText =
            searchInput.value.trim().toLowerCase();
 
        // Empty search = show all destinations
        if (!searchText) {
            displayDestinations(allDestinations);
            return;
        }
 
        const filteredDestinations =
            allDestinations.filter(place => {
 
                const placeName =
                    (place.placeName || "").toLowerCase();
 
                const location =
                    (place.location || "").toLowerCase();
 
                const category =
                    (place.category || "").toLowerCase();
 
                const subCategory =
                    (place.subCategory || "").toLowerCase();
 
                const description =
                    (place.description || "").toLowerCase();
 
                return (
                    placeName.includes(searchText) ||
                    location.includes(searchText) ||
                    category.includes(searchText) ||
                    subCategory.includes(searchText) ||
                    description.includes(searchText)
                );
 
            });
 
        console.log("Search text:", searchText);
        console.log("Search results:", filteredDestinations);
 
        displayDestinations(filteredDestinations);
 
    }
 
    // Search button
    if (searchButton) {
        searchButton.addEventListener("click", performSearch);
    }
 
    // Enter key
    searchInput.addEventListener("keydown", function (event) {
 
        if (event.key === "Enter") {
            event.preventDefault();
            performSearch();
        }
 
    });
 
    // Live search while typing
    searchInput.addEventListener("input", performSearch);
 
}
 
 
// =====================================================
// DISPLAY DESTINATIONS
// (FIX: this function header was missing, which caused
//  the SyntaxError that stopped the whole script)
// =====================================================
 
function displayDestinations(destinations) {
 
    const grid = document.getElementById("destination-grid");
 
    if (!grid) {
        console.error("destination-grid not found.");
        return;
    }
 
    grid.innerHTML = "";
 
    if (!destinations || destinations.length === 0) {
 
        grid.innerHTML = `
 
            <div class="col-span-full text-center py-10">
 
                <p class="text-text-muted">
                    No destinations found.
                </p>
 
            </div>
 
        `;
 
        return;
 
    }
 
    destinations.forEach(place => {
 
        // =================================================
        // IMAGE URL
        // =================================================
 
        const imageUrl =
            resolveImageUrl(place.imageUrl, "No+Image");
 
 
        // =================================================
        // CREATE CARD
        // =================================================
 
        const card = document.createElement("article");
 
        card.className =
            "group relative rounded-2xl " +
            "overflow-hidden shadow-sm " +
            "hover:shadow-xl " +
            "transition-all duration-300 " +
            "bg-surface-white " +
            "flex flex-col h-full";
 
        card.innerHTML = `
 
            <!-- IMAGE -->
 
            <div class="relative h-64 overflow-hidden">
 
                <img
                    class="w-full h-full object-cover
                           group-hover:scale-105
                           transition-transform duration-500"
 
                    src="${imageUrl}"
 
                    alt="${escapeHtml(place.placeName || "Tourist Place")}"
 
                    onerror="
                        this.onerror=null;
                        this.src='https://via.placeholder.com/600x400?text=No+Image';
                    "
                />
 
            </div>
 
 
            <!-- CARD CONTENT -->
 
            <div class="p-6 flex flex-col flex-grow">
 
                <h3 class="font-headline-md text-on-surface mb-2">
 
                    ${escapeHtml(place.placeName || "Tourist Place")}
 
                </h3>
 
 
                <!-- LOCATION -->
 
                <div class="flex items-center text-text-muted mb-2">
 
                    <span class="material-symbols-outlined text-[16px] mr-1">
                        location_on
                    </span>
 
                    <span>
                        ${escapeHtml(place.location || "Solapur")}
                    </span>
 
                </div>
 
 
                <!-- CATEGORY -->
 
                ${
                    place.category
                    ? `
                        <div class="text-sm text-gray-500 mb-3">
                            ${escapeHtml(place.category)}
                        </div>
                    `
                    : ""
                }
 
 
                <!-- RATING -->
 
                ${
                    place.rating !== null &&
                    place.rating !== undefined
                    ? `
                        <div class="flex items-center gap-1
                                    text-sm text-gray-600 mb-4">
 
                            <span class="material-symbols-outlined text-[16px]">
                                star
                            </span>
 
                            ${escapeHtml(String(place.rating))}
 
                        </div>
                    `
                    : ""
                }
 
 
                <!-- DETAILS BUTTON -->
 
                <button
                    type="button"
                    class="mt-auto px-6 py-2 rounded-lg
                           bg-surface-container-high
                           hover:bg-secondary
                           hover:text-on-secondary
                           transition-colors duration-300"
 
                    onclick="viewDestination(${place.placeId})">
 
                    Details
 
                </button>
 
            </div>
 
        `;
 
        grid.appendChild(card);
 
    });
 
}
 
 
// =====================================================
// VIEW DESTINATION DETAILS
// =====================================================
 
async function viewDestination(id) {
 
    console.log("Selected place ID:", id);
 
    if (!id) {
        console.error("Invalid place ID.");
        alert("Invalid tourist place.");
        return;
    }
 
    try {
 
        const url = `${API_URL}/${id}`;
 
        console.log("Loading details from:", url);
 
        const response = await fetch(url);
 
        if (!response.ok) {
            throw new Error(
                `Backend returned ${response.status} ${response.statusText}`
            );
        }
 
        const place = await response.json();
 
        console.log("Place details received:", place);
        console.log("Latitude:", place.latitude);
        console.log("Longitude:", place.longitude);
 
        // Save selected place
        localStorage.setItem(
            "selectedPlace",
            JSON.stringify(place)
        );
 
        console.log(
            "Selected place saved:",
            localStorage.getItem("selectedPlace")
        );
 
        // Open details page
        window.location.href =
            "../kyoto_destination_details/kyoto_destination_details.html";
 
    } catch (error) {
 
        console.error(
            "Error loading tourist place details:",
            error
        );
 
        alert("Unable to load tourist place details.");
 
    }
 
}
 
 
// =====================================================
// HIDDEN DESTINATIONS
// =====================================================
 
async function loadHiddenDestinations() {
 
    const grid =
        document.getElementById("hidden-destination-grid");
 
    const loading =
        document.getElementById("hidden-destination-loading");
 
    const error =
        document.getElementById("hidden-destination-error");
 
    if (!grid) {
        console.error("hidden-destination-grid not found.");
        return;
    }
 
    try {
 
        if (loading) {
            loading.classList.remove("hidden");
        }
 
        if (error) {
            error.classList.add("hidden");
        }
 
        const response = await fetch(HIDDEN_DESTINATIONS_API);
 
        if (!response.ok) {
            throw new Error(
                `Backend returned ${response.status} ${response.statusText}`
            );
        }
 
        const destinations = await response.json();
 
        console.log("Hidden destinations received:", destinations);
 
        if (loading) {
            loading.classList.add("hidden");
        }
 
        if (
            !Array.isArray(destinations) ||
            destinations.length === 0
        ) {
 
            grid.innerHTML = `
 
                <div class="col-span-full text-center py-10">
 
                    <span class="material-symbols-outlined
                                 text-text-muted text-4xl">
                        explore_off
                    </span>
 
                    <p class="text-text-muted mt-3">
                        No hidden destinations available.
                    </p>
 
                </div>
 
            `;
 
            return;
 
        }
 
        grid.innerHTML = "";
 
        destinations.forEach(place => {
 
            const imageUrl =
                resolveImageUrl(place.imageUrl, "Hidden+Destination");
 
            const card = document.createElement("article");
 
            card.className =
                "group relative " +
                "rounded-2xl overflow-hidden " +
                "shadow-sm hover:shadow-xl " +
                "transition-all duration-300 " +
                "bg-surface-white " +
                "flex flex-col h-full";
 
            card.innerHTML = `
 
                <!-- IMAGE -->
 
                <div class="relative h-60 overflow-hidden">
 
                    <img
                        src="${imageUrl}"
 
                        alt="${escapeHtml(place.placeName || "Hidden Destination")}"
 
                        class="w-full h-full object-cover
                               transition-transform duration-700
                               group-hover:scale-110"
 
                        onerror="
                            this.onerror=null;
                            this.src='https://via.placeholder.com/600x400?text=No+Image';
                        "
                    />
 
 
                    <!-- HIDDEN BADGE -->
 
                    <div class="absolute top-4 left-4
                                px-3 py-1.5 rounded-full
                                bg-primary-container/90
                                backdrop-blur-sm
                                text-white text-xs font-semibold
                                flex items-center gap-1">
 
                        <span class="material-symbols-outlined text-[15px]">
                            visibility_off
                        </span>
 
                        Hidden Gem
 
                    </div>
 
 
                    <!-- RATING -->
 
                    ${
                        place.rating !== null &&
                        place.rating !== undefined
                        ? `
                            <div class="absolute bottom-4 left-4
                                        px-3 py-1
                                        bg-primary-container/90
                                        backdrop-blur-md
                                        rounded-lg text-white text-sm
                                        flex items-center gap-1">
 
                                <span class="material-symbols-outlined text-[16px]">
                                    star
                                </span>
 
                                ${escapeHtml(String(place.rating))}
 
                            </div>
                        `
                        : ""
                    }
 
                </div>
 
 
                <!-- CONTENT -->
 
                <div class="p-6 flex flex-col flex-grow">
 
                    <h3 class="font-headline-md text-on-surface mb-2">
 
                        ${escapeHtml(place.placeName || "Hidden Destination")}
 
                    </h3>
 
 
                    <!-- LOCATION -->
 
                    <div class="flex items-center text-text-muted mb-3">
 
                        <span class="material-symbols-outlined text-[17px] mr-1">
                            location_on
                        </span>
 
                        <span>
                            ${escapeHtml(place.location || "Unknown location")}
                        </span>
 
                    </div>
 
 
                    <!-- CATEGORY -->
 
                    ${
                        place.category
                        ? `
                            <div class="text-sm text-secondary
                                        font-medium mb-3">
                                ${escapeHtml(place.category)}
                            </div>
                        `
                        : ""
                    }
 
 
                    <!-- DESCRIPTION -->
 
                    <p class="text-sm text-on-surface-variant
                              line-clamp-3 mb-6">
 
                        ${escapeHtml(
                            place.description ||
                            "Discover this beautiful hidden destination."
                        )}
 
                    </p>
 
 
                    <!-- DETAILS -->
 
                    <button
                        type="button"
                        class="mt-auto w-full px-6 py-3 rounded-lg
                               bg-surface-container-high
                               text-on-surface
                               hover:bg-secondary
                               hover:text-on-secondary
                               transition-colors duration-300"
 
                        onclick="viewDestination(${place.placeId})">
 
                        View Details
 
                    </button>
 
                </div>
 
            `;
 
            grid.appendChild(card);
 
        });
 
    } catch (errorObject) {
 
        console.error(
            "Error loading hidden destinations:",
            errorObject
        );
 
        if (loading) {
            loading.classList.add("hidden");
        }
 
        if (error) {
            error.classList.remove("hidden");
        }
 
    }
 
}
 
 
// =====================================================
// AI ASSISTANT: TOGGLE WINDOW
// =====================================================
 
function toggleAIAssistant() {
 
    const chatWindow =
        document.getElementById("ai-assistant-window");
 
    const openIcon =
        document.getElementById("ai-open-icon");
 
    const closeIcon =
        document.getElementById("ai-close-icon");
 
    if (!chatWindow) {
        return;
    }
 
    const isHidden = chatWindow.classList.contains("hidden");
 
    if (isHidden) {
 
        chatWindow.classList.remove("hidden");
        chatWindow.classList.add("flex");
 
        if (openIcon) {
            openIcon.classList.add("hidden");
        }
 
        if (closeIcon) {
            closeIcon.classList.remove("hidden");
        }
 
        const input = document.getElementById("ai-chat-input");
 
        if (input) {
            setTimeout(function () {
                input.focus();
            }, 200);
        }
 
    } else {
 
        chatWindow.classList.add("hidden");
        chatWindow.classList.remove("flex");
 
        if (openIcon) {
            openIcon.classList.remove("hidden");
        }
 
        if (closeIcon) {
            closeIcon.classList.add("hidden");
        }
 
    }
 
}
 
 
// =====================================================
// AI ASSISTANT: ADD USER MESSAGE
// =====================================================
 
function addAIUserMessage(message) {
 
    const messages =
        document.getElementById("ai-chat-messages");
 
    if (!messages) {
        return;
    }
 
    const wrapper = document.createElement("div");
 
    wrapper.className = "ai-message flex justify-end";
 
    wrapper.innerHTML = `
 
        <div class="max-w-[80%] bg-secondary text-on-secondary
                    rounded-2xl rounded-tr-none px-4 py-3">
 
            <p class="text-sm whitespace-pre-wrap">
                ${escapeHtml(message)}
            </p>
 
        </div>
 
    `;
 
    messages.appendChild(wrapper);
 
    scrollAIChatToBottom();
 
}
 
 
// =====================================================
// AI ASSISTANT: ADD AI MESSAGE
// =====================================================
 
function addAIMessage(message) {
 
    const messages =
        document.getElementById("ai-chat-messages");
 
    if (!messages) {
        return;
    }
 
    const wrapper = document.createElement("div");
 
    wrapper.className = "ai-message flex items-start gap-2";
 
    wrapper.innerHTML = `
 
        <div class="w-8 h-8 rounded-full bg-secondary/10
                    flex items-center justify-center flex-shrink-0">
 
            <span class="material-symbols-outlined
                         text-secondary text-[18px]">
                smart_toy
            </span>
 
        </div>
 
        <div class="max-w-[80%] bg-surface-white
                    rounded-2xl rounded-tl-none
                    px-4 py-3 shadow-sm">
 
            <p class="text-sm text-on-surface whitespace-pre-wrap">
                ${escapeHtml(message)}
            </p>
 
        </div>
 
    `;
 
    messages.appendChild(wrapper);
 
    scrollAIChatToBottom();
 
}
 
 
// =====================================================
// AI ASSISTANT: SCROLL CHAT
// =====================================================
 
function scrollAIChatToBottom() {
 
    const messages =
        document.getElementById("ai-chat-messages");
 
    if (!messages) {
        return;
    }
 
    messages.scrollTop = messages.scrollHeight;
 
}
 
 
// =====================================================
// AI ASSISTANT: SEND MESSAGE
// =====================================================
 
async function sendAIMessage() {
 
    const input =
        document.getElementById("ai-chat-input");
 
    const sendButton =
        document.getElementById("ai-send-button");
 
    const typing =
        document.getElementById("ai-typing");
 
    if (!input) {
        return;
    }
 
    const message = input.value.trim();
 
    if (!message) {
        return;
    }
 
    // Show user message
    addAIUserMessage(message);
 
    // Clear input
    input.value = "";
 
    // Disable send button
    if (sendButton) {
        sendButton.disabled = true;
        sendButton.classList.add("opacity-50");
    }
 
    // Show typing
    if (typing) {
        typing.classList.remove("hidden");
    }
 
    scrollAIChatToBottom();
 
    try {
 
        aiConversation.push({
            role: "user",
            content: message
        });
 
        const response = await fetch(AI_ASSISTANT_API, {
 
            method: "POST",
 
            headers: {
                "Content-Type": "application/json"
            },
 
            body: JSON.stringify({
                message: message,
                history: aiConversation
            })
 
        });
 
        if (!response.ok) {
            throw new Error(
                `AI backend returned ${response.status}`
            );
        }
 
        const data = await response.json();
 
        const aiResponse = data.response;
        if (typeof aiResponse !== "string" || !aiResponse.trim()) {
            throw new Error("AI backend returned an empty response.");
        }
 
        aiConversation.push({
            role: "assistant",
            content: aiResponse
        });
 
        addAIMessage(aiResponse);
 
    } catch (error) {
 
        console.error("AI Assistant Error:", error);
        aiConversation.pop();
        addAIMessage(error.message || "Sorry, I am unable to connect to the AI Assistant right now.");
 
    } finally {
 
        if (typing) {
            typing.classList.add("hidden");
        }
 
        if (sendButton) {
            sendButton.disabled = false;
            sendButton.classList.remove("opacity-50");
        }
 
        input.focus();
 
    }
 
}
 
 
// =====================================================
// INITIALIZE AI ASSISTANT
// =====================================================
 
function initializeAIAssistant() {
 
    const assistantButton =
        document.getElementById("ai-assistant-button");
 
    const closeButton =
        document.getElementById("ai-close-button");
 
    const sendButton =
        document.getElementById("ai-send-button");
 
    const input =
        document.getElementById("ai-chat-input");
 
    const navAI =
        document.getElementById("nav-ai-assistant");
 
    // Floating button
    if (assistantButton) {
        assistantButton.addEventListener("click", toggleAIAssistant);
    }
 
    // Close button
    if (closeButton) {
        closeButton.addEventListener("click", toggleAIAssistant);
    }
 
    // Send button
    if (sendButton) {
        sendButton.addEventListener("click", sendAIMessage);
    }
 
    // Enter key
    if (input) {
 
        input.addEventListener("keydown", function (event) {
 
            if (event.key === "Enter" && !event.shiftKey) {
                event.preventDefault();
                sendAIMessage();
            }
 
        });
 
    }
 
    // Header AI Assistant link
    if (navAI) {
 
        navAI.addEventListener("click", function (event) {
 
            event.preventDefault();
 
            const chatWindow =
                document.getElementById("ai-assistant-window");
 
            if (
                chatWindow &&
                chatWindow.classList.contains("hidden")
            ) {
                toggleAIAssistant();
            }
 
        });
 
    }
 
}
 
 
// =====================================================
// START APPLICATION
// =====================================================
 
document.addEventListener("DOMContentLoaded", function () {
 
    console.log("TravelBuddy Explore page initialized.");
 
    // Main destinations
    loadDestinations();
 
    // Hidden destinations
    loadHiddenDestinations();
 
    // AI assistant
    initializeAIAssistant();
 
    // Destination search
    setupDestinationSearch();
 
});
 