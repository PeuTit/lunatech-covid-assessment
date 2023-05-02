#!/usr/bin/env bash

Bold='\033[1m'   # Bold
NC='\033[0m'     # No Color

# To change this banner, go to http://patorjk.com/software/taag/#p=display&f=Big&t=Lunatech%0ACOVID%0AAssessment
cat << "EOF"
  _                       _            _                  
 | |                     | |          | |                 
 | |    _   _ _ __   __ _| |_ ___  ___| |__               
 | |   | | | | '_ \ / _` | __/ _ \/ __| '_ \              
 | |___| |_| | | | | (_| | ||  __/ (__| | | |             
 |______\__,_|_| |_|\__,_|\__\___|\___|_| |_|             
   _____ ______      _______ _____                        
  / ____/ __ \ \    / /_   _|  __ \                       
 | |   | |  | \ \  / /  | | | |  | |                      
 | |   | |  | |\ \/ /   | | | |  | |                      
 | |___| |__| | \  /   _| |_| |__| |                  _   
  \__/\_\____/   \/   |_____|_____/                  | |  
    /  \   ___ ___  ___  ___ ___ _ __ ___   ___ _ __ | |_ 
   / /\ \ / __/ __|/ _ \/ __/ __| '_ ` _ \ / _ \ '_ \| __|
  / ____ \\__ \__ \  __/\__ \__ \ | | | | |  __/ | | | |_ 
 /_/    \_\___/___/\___||___/___/_| |_| |_|\___|_| |_|\__|
                                                          
EOF

echo -e "${Bold}Initializing schema...${NC}"
psql --host=postgres --username=postgres -w -d lunatech_covid -f ./schema.sql &&
echo -e "\n${Bold}Initializing countries...${NC}"
psql --host=postgres --username=postgres -w -d lunatech_covid -c "\copy countries(id, code, name, population, continent, wikipedia_link, keywords) FROM 'countries.csv' DELIMITER ',' CSV HEADER"
echo -e "\n${Bold}Initializing cases...${NC}"
psql --host=postgres --username=postgres -w -d lunatech_covid -c "\copy cases(recorded_date,infections,deaths,iso_country) FROM 'cases.csv' DELIMITER ',' CSV HEADER"
echo -e "\n${Bold}Initializing vaccinations...${NC}"
psql --host=postgres --username=postgres -w -d lunatech_covid -c "\copy vaccinations(recorded_date,daily_vaccinations_raw,daily_vaccinations,iso_country) FROM 'vaccinations.csv' DELIMITER ',' CSV HEADER"

# To change this banner, go to http://patorjk.com/software/taag/#p=display&f=Big&t=DB%20ready%20to%20use!
cat << "EOF"
  _____  ____                       _         _                         _ 
 |  __ \|  _ \                     | |       | |                       | |
 | |  | | |_) |  _ __ ___  __ _  __| |_   _  | |_ ___    _   _ ___  ___| |
 | |  | |  _ <  | '__/ _ \/ _` |/ _` | | | | | __/ _ \  | | | / __|/ _ \ |
 | |__| | |_) | | | |  __/ (_| | (_| | |_| | | || (_) | | |_| \__ \  __/_|
 |_____/|____/  |_|  \___|\__,_|\__,_|\__, |  \__\___/   \__,_|___/\___(_)
                                       __/ |                              
                                      |___/                               
EOF
