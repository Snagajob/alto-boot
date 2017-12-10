
default_labels = [
    "admin and paraprofessional",
    "animal services",
    "automotive services",
    "beauty and grooming services",
    "childcare and early education",
    "corporate professional",
    "corporate sales and marketing professional",
    "direct sales and customer support",
    "environmental services",
    "exclude spanish language",
    "food and beverage",
    "health and wellness direct care",
    "health and wellness home care",
    "health and wellness professional",
    "health and wellness technical",
    "hospitality",
    "industrial warehouse and manufacturing front line",
    "industrial warehouse and manufacturing professional",
    "industrial warehouse and manufacturing skilled/technical",
    "information technology professional",
    "personal instruction and tutoring",
    "personal services and instruction",
    "real estate sales and related services",
    "renovation contracting construction commercial",
    "renovation contracting construction home",
    "retail",
    "retail grocery",
    "security services",
    "trucking and transportation general",
    "trucking and transportation specialized licensed"
];

function loadDefaultLabels(){
    for (i=0; i<default_labels.length; i++) {
        addLabelName(default_labels[i]);
    }
}

