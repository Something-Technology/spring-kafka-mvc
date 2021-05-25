# Spring Kafka MVC
This is an alternative approach to integrate Apache Kafka into a Spring Project.
We don't need a separate mapping for Consumers and Producers here. Registration of topics, consuming 
and producing of messages are handled in the Controller. A Topic provider helps us to manage the mapping 
of Records to the associated topics. 

## Setup
Simple initialize a new Spring boot project. If you use gradle just add this library in your build gradle configuration.
Create a new Application configuration in your project where you configure the consumer and producer factories, 
like shown as an Example in the test package of this project. 

## Contribute
- Source Code https://github.com/Something-Technology/spring-kafka-mvc

## License
This project is licensed under the [MIT] License. 
