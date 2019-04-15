# Spring Security + JPA

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Kas gali būti svarbu

- __Reikia sukurti _User_ entity__

- __Reikia sukurti servisą/klasę, kuri implementuoja _UserDetailsService_.__
    
    Joje reikia realizuoti tik vieną metodą __loadUserByUsername__, kuris turi grąžinti __UserDetails__
    objektą pagal __username__ parametrą. Paprastai ieškome DB _user_ lentelėje įrašo pagal pateiktą
    __username__ ir formuojame objektą arba tiesiai iš interfeiso __UserDetails__ 
    arba grąžiname klasės (pvz __AppUserDetail__),
    kuri šį interfeisą implementuoja, objektą. 
    
- __Aprašome _PasswordEncoder_ _@Bean_.__
  
  Paprastai naudojame __BCrypt__ metodą užkoduoti slaptažodžius.
  
  
### Pastabos

- Klasėje __MVCConfig__ aprašyta __/error__ nuoroda, kad rodytų ne standartinį bet mūsų puslapį klaidos atveju

- Klasėje __SecurityConfig__ metode __configure(HttpSecurity http)__ nurodyta, kad pagrindinis puslapis (__"/"__)
    būtų prieinamas visiems ir taip pat kad reikalingi __login__ ir __logout__ langai. 
    
- Parodyta kaip __/any__ užklausos metu galima keliais būdais gauti prisijungusio userio vardą ir daugiau info apie userį
    (pažiūrėkit __Ctrl__ klasės __String any(...)__ metodą) 
    [Daugiau info ...](https://www.baeldung.com/get-user-in-spring-security)

