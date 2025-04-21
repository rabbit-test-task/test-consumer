# test-consumer (Java)
- Java 17, Spring Boot 3.4.4
- Spring AMQP для интеграции с RabbitMQ
- Spring Data JPA с H2 в качестве БД
- WebSockets для передачи сообщений
- Thymeleaf для рендеринга страниц
- Docker
- CI/CD с Github Actions

Принимает 2 типа сообщений:
```
{"datetime_now": datetime}
```
где datetime - текущая дата и время (в utc)


```
{"id" : i , "value": j}
```
где i - автоинкремент (идентификатор сообщения), прибавляющий 1 к значению с каждым новым сообщением.
j – случайное целочисленное число.

Распознает тип сообщений, сохраняет их в бд и отображает на странице.

### Переменные окружения
```
# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_QUEUE=message-queue
RABBITMQ_EXCHANGE=message-exchange
RABBITMQ_ROUTING_KEY=message-routing-key

# База данных
DB_USERNAME=sa
DB_PASSWORD=password
```

### Направления дальнейшего развития/улучшения:
- Заменить H2 (InMemory) БД на нормальную (Posgre/Mysql)
- Реализовать механизм повторной обработки элемента
- Добавить механизм отслеживания доставки сообщений
- Добавить мониторинг (Grafana/Prometheus)
- Развитие в целом панели (добавление авторизации, ролей, механизм обработки сообщений, а не просто отображение в панели администратора и т.д)
- Написание интеграционных тестов

![изображение](https://github.com/user-attachments/assets/cef0bd99-4478-460c-bdd4-69a85a17785e)
