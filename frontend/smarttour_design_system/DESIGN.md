---
name: SmartTour Design System
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#45464d'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#006a61'
  on-secondary: '#ffffff'
  secondary-container: '#86f2e4'
  on-secondary-container: '#006f66'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#2a1700'
  on-tertiary-container: '#b87500'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#89f5e7'
  secondary-fixed-dim: '#6bd8cb'
  on-secondary-fixed: '#00201d'
  on-secondary-fixed-variant: '#005049'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
  surface-white: '#FFFFFF'
  text-main: '#0F172A'
  text-muted: '#64748B'
  glass-stroke: rgba(255, 255, 255, 0.3)
typography:
  headline-xl:
    fontFamily: Montserrat
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Montserrat
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Montserrat
    fontSize: 28px
    fontWeight: '700'
    lineHeight: '1.2'
  headline-md:
    fontFamily: Montserrat
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: '1.2'
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 64px
  container-max: 1280px
---

## Brand & Style
The design system for SmartTour embodies a **Modern Corporate** aesthetic with **Glassmorphic** nuances. It is designed to feel sophisticated yet adventurous, catering to tech-savvy travelers who value reliability and high-end experiences. 

The visual narrative prioritizes high-quality photography and expansive white space, creating a "breathable" luxury atmosphere. Key characteristics include:
- **Precision Tech:** Sharp typography and clean grids denote technical competence.
- **Adventurous Vibrancy:** Strategic use of teal and orange accents to evoke the excitement of travel.
- **Lustrous Depth:** Semi-transparent surfaces and blurred backgrounds provide a premium, multi-layered digital environment.

## Colors
This design system utilizes a high-contrast palette rooted in **Deep Oceanic Blue** for structural integrity and authority. 

- **Primary:** Used for navigation bars, primary headings, and heavy UI anchors.
- **Secondary (Teal):** The "action" color for booking flows, success states, and primary CTAs.
- **Tertiary (Orange):** Used sparingly for urgent highlights, price tags, and secondary interaction points.
- **Neutral/Background:** A cool-toned off-white (#F8FAFC) reduces eye strain and distinguishes content cards from the base canvas.

## Typography
The typographic hierarchy balances the geometric boldness of **Montserrat** for display roles with the utilitarian clarity of **Inter** for functional text. 

Headlines should utilize tight tracking (letter-spacing) to maintain a modern, "locked-in" appearance. Body text requires generous line height (1.5–1.6) to ensure legibility during long-form destination reading. For labels and metadata, use Inter with slightly increased tracking and medium-to-bold weights to ensure visibility against photographic backgrounds.

## Layout & Spacing
The layout follows a **12-column fluid grid** for desktop and a **4-column grid** for mobile. 

- **Vertical Rhythm:** Built on a 4px baseline shift. Use increments of 8px (8, 16, 24, 32, 48, 64) for logical spacing between sections.
- **Margins:** Desktop views should maintain a wide margin (64px) to emphasize the premium, spacious feel.
- **Reflow:** On tablet/mobile, cards should transition from multi-column grids to a single-column stack, increasing internal card padding to 20px for touch-target optimization.

## Elevation & Depth
Depth is created through **Glassmorphism** rather than traditional heavy shadows. 

1.  **Background Layer:** The base neutral grey (#F8FAFC).
2.  **Surface Layer (Cards/Modals):** Pure white (#FFFFFF) with 80-90% opacity and a `backdrop-filter: blur(12px)`. 
3.  **Borders:** Use 1px solid outlines in `glass-stroke` (low-opacity white) to define edges on light backgrounds, or `primary` at 10% opacity on dark backgrounds.
4.  **Shadows:** Use a single, soft ambient shadow: `0 4px 20px -2px rgba(15, 23, 42, 0.08)`. This mimics natural light without making the UI feel "heavy."

## Shapes
This design system utilizes a **Rounded** shape language to feel approachable and friendly. 

- **Standard Cards:** 16px (rounded-lg) to accommodate large-scale photography.
- **Buttons & Inputs:** 8px (base) for a professional, structured feel.
- **Feature Highlights/Images:** 24px (rounded-xl) for a softer, more organic aesthetic in editorial sections.

## Components

### Buttons
- **Primary:** Caribbean Teal (#0D9488) with white text. High-radius (8px) and a subtle scale-up interaction on hover.
- **Secondary:** Transparent background with a 1.5px Teal border. 
- **Tertiary:** Pure white glassmorphic effect with 1px border for use over photography.

### Cards
- **Destination Cards:** Feature full-bleed images with a 16px corner radius. Text should be housed in a glassmorphic footer overlaying the bottom 30% of the image.
- **Info Cards:** White background, soft shadow, 16px radius, and 24px internal padding.

### Inputs
- **Search Bars:** Pill-shaped (32px radius) with a light grey fill (#F1F5F9). On focus, transition to a white background with a 2px Teal border.
- **Selection Chips:** 8px radius, teal background (10% opacity) with teal text for active states.

### Iconography
- **Style:** Lucide-style (2px stroke width, rounded caps and joins).
- **Usage:** Icons should be framed in small 32x32px circles with a 5% primary color tint for better visual grouping in lists.

### Interactions
- **Page Transitions:** Gentle fade-in with a 20px upward slide for new content blocks.
- **Hover States:** Subtle 1.02x scale for cards and 0.98x scale for buttons to provide tactile feedback.