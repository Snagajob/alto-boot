
default_labels = [
    "childcare and early education",
    "corporate professional",
    "information technology professional",
    "admin and paraprofessional",
    "health and wellness professional",
    "health and wellness technical",
    "health and wellness direct care",
    "health and wellness home care",
    "industrial warehouse and manufacturing professional",
    "industrial warehouse and manufacturing skilled/technical",
    "industrial warehouse and manufacturing front line",
    "trucking and transportation licensed",
    "trucking and transportation general",
    "hospitality",
    "food service",
    "retail",
    "retail grocery",
    "home renovation contracting construction",
    "commercial renovation contraction construction",
    "personal services and instruction",
    "corporate sales and marketing professional",
    "direct sales and support",
    "security services",
    "animal services",
    "automotive services",
    "beauty and grooming services",
    "personal instruction and tutoring",

];

function loadDefaultLabels(){
    for (i=0; i<default_labels.length; i++) {
        addLabelName(default_labels[i]);
    }
}

