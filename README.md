For Mainak: 

    git clone -b dev/Monka https://github.com/MainakMukherjee01/LeaveScheduler.git



For Samaresh: 

    git clone -b dev/Somu https://github.com/MainakMukherjee01/LeaveScheduler.git



For Soumik: 

    git clone -b dev/Somku https://github.com/MainakMukherjee01/LeaveScheduler.git



Until Tests are fixed:

    Clean Install:  mvn clean install -DskipTests  
              Run:  mvn spring-boot:run -DskipTests



In project Root-Repo create a .env file with fields:
    
    APP_JWT_SECRET=<YOUR_JWT_SECRET>
    SPRING_DATASOURCE_USERNAME=<YOUR_H2_DB_USERNAME>
    SPRING_DATASOURCE_PASSWORD=<YOUR_H2_DB_PASSWORD>
    SPRING_MAIL_USERNAME=<YOUR_SMTP_ENABLED_MAIL_USERNAME>
    SPRING_MAIL_PASSWORD=<YOUR_SMTP_ENABLED_MAIL_PASSWORD>
