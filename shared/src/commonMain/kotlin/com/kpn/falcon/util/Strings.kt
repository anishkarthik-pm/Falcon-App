package com.kpn.falcon.util

object Strings {
    // App
    const val APP_NAME = "KPN Falcon"

    // Navigation tabs
    const val TAB_HOME = "Home"
    const val TAB_PROPERTIES = "Properties"
    const val TAB_PIPELINE = "Pipeline"
    const val TAB_TASKS = "Tasks"

    // Home screen
    const val HOME_DASHBOARD = "Your Dashboard"
    const val HOME_OPEN_PROPERTIES = "Open Properties"
    const val HOME_PENDING_PROPERTIES = "Pending Properties"
    const val HOME_CLOSED_PROPERTIES = "Closed Properties"
    const val HOME_IN_PROGRESS = "In Progress"
    const val HOME_RECENT_PROPERTIES = "Recent Properties"
    const val HOME_RECENT_ACTIVITIES = "Recent Activities"
    const val HOME_ADD_PROPERTY = "+ Add Property"
    const val HOME_VIEW_KANBAN = "⊞ View Kanban"
    const val HOME_STORES_FINALIZED = "Stores finalized"

    // Properties screen
    const val PROPERTIES_TITLE = "Properties"
    const val PROPERTIES_SEARCH_HINT = "Search by ID, address, city..."
    const val PROPERTIES_EMPTY_TITLE = "No properties yet!"
    const val PROPERTIES_EMPTY_SUBTITLE = "Add your first property to get started"
    const val PROPERTIES_FILTER_ALL = "All"
    const val PROPERTIES_FILTER_OPEN = "Open"
    const val PROPERTIES_FILTER_PENDING = "Pending"
    const val PROPERTIES_FILTER_IN_PROGRESS = "In-Progress"
    const val PROPERTIES_FILTER_CLOSED = "Closed"

    // Add property wizard
    const val WIZARD_TITLE = "Add New Property"
    const val WIZARD_STEP_LOCATION = "Location"
    const val WIZARD_STEP_STORE_SPECS = "Store Specs"
    const val WIZARD_STEP_ROAD = "Road & Access"
    const val WIZARD_STEP_MEDIA = "Media"
    const val WIZARD_STEP_COMMERCIALS = "Commercials"
    const val WIZARD_STEP_COMPETITION = "Competition"
    const val WIZARD_STEP_CONTACT = "Contact"

    // Step 1 – Location
    const val LOCATION_HEADER = "📍 Location & Address — Capture the property location using GPS"
    const val LOCATION_CAPTURE_GPS = "Capture GPS Location"
    const val LOCATION_GPS_CAPTURED = "GPS Captured ✓"
    const val LOCATION_FULL_ADDRESS = "Full Address"
    const val LOCATION_NEAREST_LANDMARK = "Nearest Landmark"
    const val LOCATION_CITY = "City"
    const val LOCATION_STATE = "State"
    const val LOCATION_PINCODE = "Pincode"

    // Step 2 – Store Specs
    const val STORE_SPECS_HEADER = "🏪 Store Specifications — Enter property dimensions & features"
    const val STORE_SPECS_SITE_STATUS = "Site Status"
    const val STORE_SPECS_ROAD_FACING = "Main Road or Side Road"
    const val STORE_SPECS_FLOORS = "Number of Floors"
    const val STORE_SPECS_TOTAL_AREA = "Total Store Area"
    const val STORE_SPECS_CARPET_AREA = "Carpet Area"
    const val STORE_SPECS_STEPS = "Steps to Entry"
    const val STORE_SPECS_CEILING_HEIGHT = "Ceiling / Beam Bottom Height"
    const val STORE_SPECS_STORE_FRONTAGE = "Store Frontage"
    const val STORE_SPECS_FACADE_FRONTAGE = "Facade Frontage"
    const val STORE_SPECS_DIMENSIONS = "Store Dimensions"
    const val STORE_SPECS_INFRASTRUCTURE = "Existing Infrastructure"
    const val STORE_SPECS_SIGNAGE = "Signage Availability"
    const val STORE_SPECS_SIGNAGE_WIDTH = "Signage Width"
    const val STORE_SPECS_ADD_SIGNAGE = "Additional Signage Required"
    const val STORE_SPECS_JUICE_COUNTER = "Juice Counter Available?"
    const val STORE_SPECS_JUICE_AREA = "Juice Counter Area"
    const val STORE_SPECS_POWER_LOAD = "Power Load"

    // Step 3 – Road
    const val ROAD_HEADER = "🚗 Road & Accessibility — Capture dimensions, parking & delivery access"
    const val ROAD_FRONT_WIDTH = "Front Road Width"
    const val ROAD_TYPE = "Road Type"
    const val ROAD_CONNECTIVITY = "Road Connectivity"
    const val ROAD_PARKING_AVAILABLE = "Parking Area Available?"
    const val ROAD_DELIVERY_ACCESS = "Delivery Vehicle Access"
    const val ROAD_CAR_PARKING = "Car Parking No."
    const val ROAD_BIKE_PARKING = "Bike/Scooter Parking No."
    const val ROAD_MIN_CAR_PARKING_HINT = "Minimum 4 Required"

    // Step 4 – Media
    const val MEDIA_HEADER = "📷 Media & Document Uploads — Capture photos of the property from all angles"
    const val MEDIA_EXTERIOR = "Exterior Photos (min 4)"
    const val MEDIA_INTERNAL = "Internal Photos (min 3)"
    const val MEDIA_COMPETITOR = "Competitor Photos (min 3)"
    const val MEDIA_VIDEOS = "Add Videos (min 3)"
    const val MEDIA_DOCUMENTS = "Add Documents"

    // Step 5 – Commercials
    const val COMMERCIALS_HEADER = "💼 Commercial Details — Rent negotiations, lease terms & financial metrics"
    const val COMMERCIALS_LANDLORD_RENT = "Proposed Rent by Landlord"
    const val COMMERCIALS_BD_RENT = "Offered Rent by BD Team"
    const val COMMERCIALS_REVENUE_ESTIMATE = "Revenue Estimate"
    const val COMMERCIALS_SECURITY_DEPOSIT = "Security Deposit"
    const val COMMERCIALS_LEASE_TERM = "Total Lease Term"
    const val COMMERCIALS_ESCALATION = "Escalation"
    const val COMMERCIALS_RENT_FREE = "Rent Free Period"
    const val COMMERCIALS_LESSEE_LOCK_IN = "Lessee Lock-in"
    const val COMMERCIALS_LESSOR_LOCK_IN = "Lessor Lock-in"
    const val COMMERCIALS_POSSESSION_DATE = "Possession Date"
    const val COMMERCIALS_OPENING_MONTH = "Opening Month"
    const val COMMERCIALS_OPENING_QUARTER = "Opening Quarter"
    const val COMMERCIALS_REGISTRATION_FEES = "Registration Fees"
    const val COMMERCIALS_DEVIATION = "Deviation From Std Terms"
    const val COMMERCIALS_ADJUSTABLE_ADVANCE = "Adjustable Advance"
    const val COMMERCIALS_PRE_FILLED_CHIP = "5 Pre-Filled KPN Std"

    // Step 6 – Competition
    const val COMPETITION_HEADER = "👥 Competition Mapping — Document 3–5 nearby competitor stores"
    const val COMPETITION_ADD = "+ Add Competitor"
    const val COMPETITION_ADD_COMPETITOR = "+ Add Competitor"
    const val COMPETITION_BRAND = "Brand Name"
    const val COMPETITION_BRAND_NAME = "Brand Name"
    const val COMPETITION_DISTANCE = "Distance"
    const val COMPETITION_AREA = "Store Area"
    const val COMPETITION_STORE_AREA = "Store Area"
    const val COMPETITION_RENT = "Rent per sqft"
    const val COMPETITION_RENT_SQFT = "Rent per sqft"
    const val COMPETITION_SALES = "Sales/month"
    const val COMPETITION_EMPTY = "No competitors added yet"
    const val COMPETITION_EMPTY_HINT = "Add at least 3–5 nearby competitor stores"
    const val COMPETITION_NOTE = "Include all grocery & FMCG stores within 500m radius"

    // Step 7 – Contact
    const val CONTACT_HEADER = "👤 Contact Information — Landlord & broker details of this property"
    const val CONTACT_SOURCE_SECTION = "Contact Source"
    const val CONTACT_LANDLORD_SECTION = "Landlord Details"
    const val CONTACT_BROKER_SECTION = "Broker Details"
    const val CONTACT_LANDLORD_NAME = "Landlord Name"
    const val CONTACT_LANDLORD_PHONE = "Landlord Phone"
    const val CONTACT_LANDLORD_EMAIL = "Landlord Email"
    const val CONTACT_BROKER_NAME = "Broker Name"
    const val CONTACT_BROKER_PHONE = "Broker Phone"
    const val CONTACT_SUMMARY_TITLE = "Submission Summary"
    const val CONTACT_DEVIATION_WARNING = "⚠ Deviations from KPN standard terms will require BD Manager approval"

    // Submit
    const val SUBMIT = "Submit"
    const val SUBMIT_SUCCESS = "Submitted ✓"
    const val DONE = "Done"

    // Validation
    const val DEVIATION_FROM_BASELINE = "Deviation from baseline"
    const val DEVIATION_FROM_STD = "Deviation from Std"
    const val REQUIRED_FIELD = "This field is required"
    const val INVALID_PHONE = "Enter a valid 10-digit phone number"

    // Proximity
    const val PROXIMITY_SAFE = "✓ No KPN Store within 3km radius."
    const val PROXIMITY_WARNING = "⚠ KPN Store within 3km radius!"

    // Property detail tabs
    const val DETAIL_TAB_OVERVIEW = "Overview"
    const val DETAIL_TAB_GEO_IQ = "GeoIQ report"
    const val DETAIL_TAB_SCORING = "Scoring"
    const val DETAIL_TAB_COMMENTS = "Comments"
    const val DETAIL_TAB_APPROVALS = "Approvals"

    // Property detail — section titles
    const val DETAIL_SECTION_STORE_SPECS = "Store Specifications"
    const val DETAIL_SECTION_ROAD = "Road & Accessibility"
    const val DETAIL_SECTION_COMMERCIALS = "Commercial Details"
    const val DETAIL_SECTION_COMPETITORS = "Competitors"
    const val DETAIL_SECTION_CONTACT = "Contact Information"
    const val DETAIL_APPROVAL_TRAIL = "Approval Trail"
    const val DETAIL_MEDIA = "Media"
    const val DETAIL_PHASE_LABEL = "Approval Phase"

    // Common
    const val LOADING = "Loading..."
    const val RETRY = "Retry"
    const val BACK = "Back"
    const val NEXT = "Next"
    const val CANCEL = "Cancel"
    const val SAVE = "Save"
    const val DELETE = "Delete"
    const val EDIT = "Edit"
    const val OFFLINE_BANNER = "No internet connection — working offline"
    const val PROPERTY_ID_PENDING = "Pending"
    const val SQFT = "sq ft"
    const val FT = "ft"
    const val KVA = "KVA"
    const val MONTH = "month"
    const val YEARS = "years"
    const val METER = "m"

    // Delete confirmation
    const val DELETE_CONFIRM_TITLE = "Delete Property"
    const val DELETE_CONFIRM_MESSAGE = "Are you sure you want to delete property %s? This action cannot be undone."
    const val DELETE_CONFIRM_CTA = "Yes, Delete"
    const val DELETE_CANCEL = "Cancel"
}
