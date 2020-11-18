# springboot-notice-project
공지사항 웹 어플리케이션


### 공통  
![Generic badge](https://img.shields.io/badge/springboot-2.2.6-brightgreen.svg)
![Generic badge](https://img.shields.io/badge/h2-1.4.200-blue.svg)
![Generic badge](https://img.shields.io/badge/jdk-1.8-orange.svg)
![Generic badge](https://img.shields.io/badge/Gradle-6.2.2-yellowgreen.svg)
![Generic badge](https://img.shields.io/badge/eclipse-2020/03-purple.svg)

### mybatis  
![Generic badge](https://img.shields.io/badge/mybatis-3.5.4-green.svg)

### jpa  
![Generic badge](https://img.shields.io/badge/spring--data--jpa-2.2.6-blueviolet.svg)
![Generic badge](https://img.shields.io/badge/hibernate--core-5.4.12.Final-ff69b4.svg)
![Generic badge](https://img.shields.io/badge/querydsl-4.2.2-bbd.svg)
## 실행 방법

Windows:

```sh
./gradlew clean bootRun --args='--spring.profiles.active=windows'
```

OS X or Linux:

```sh
./gradlew clean bootRun --args='--spring.profiles.active=mac'
```

## 테스트 방법

```sh
./gradlew clean test
```

## 빌드 및 jar 실행 방법

```sh
./gradlew clean build

java -jar notice-project-1.0.0.jar --spring.profiles.active=windows
java -jar notice-project-1.0.0.jar --spring.profiles.active=mac
```

## 접속 URL
http://localhost
