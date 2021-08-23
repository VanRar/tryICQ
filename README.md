# Курсовой проект "Сетевой чат"

## Описание проекта

Разработано два приложения для обмена текстовыми сообщениями по сети с помощью консоли (терминала) между двумя и более пользователями. 

**Первое приложение - сервер чата**, ожидает подключения пользователей.

**Второе приложение - клиент чата**, подключается к серверу чата и осуществляет доставку и получение новых сообщений.

Все сообщения должны записываться в file.log как на сервере (server.log), так и на клиентах(client.log). File.log дополняется при каждом запуске, а также при отправленном или полученном сообщении. Выход из чата осуществлен по команде exit.

## Описание сервера

- Установка порта для подключения клиентов через файл настроек (settings.properties);
- подключение к серверу в любой момент и присоединиться к чату;
- Отправка новых сообщений клиентам;
- Запись всех отправленных через сервер сообщений с указанием имени пользователя и времени отправки.

## Описание клиента

- Выбор имени для участия в чате;
- Читает настройки приложения из файла настроек (settings.properties) - хостинг, номер порта сервера;
- Подключение к указанному в настройках серверу;
- Для выхода из чата нужно набрать команду выхода - “/exit”;
- Каждое сообщение участников записывается в файл логирования (client.log). При каждом запуске приложения файл дополняется.

## Описание реализации

+ Сервер одновременно ожидает новых пользователей и обрабатывает поступающие сообщения от пользователей;
+ Использован сборщик пакетов maven;
+ Код размещен на github;
- Код покрыт unit-тестами. прим. в процессе

## Шаги реализации:

**1. Схема приложений**
![diagram (2)](https://user-images.githubusercontent.com/67290161/130491038-a6b622f1-aa03-4fa6-bbd4-3838505996c8.png)
P.S.: блок-схема не по госту, но она тут больше как что-то среднее между схемой приложения и структурной

** 2. Описание архитектуры приложений**
(сколько потоков за что отвечают, придумать протокол обмена сообщениями между приложениями);
** 3. Репозиторий проекта на github, Вы в нём;**
** 4. Сервер написан;**
** 5. Проведен интеграционный тест сервера, с помощью telnet;**
** 6. Клиент написан;**
** 7. Проведен интеграционный тест сервера и клиента;**
** 8. Сервер протестирован при подключении нескольких клиентов;**
** 9. Написан README.md к проекту;**
~~10. Отправить на проверку.
