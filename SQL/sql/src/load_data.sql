COPY USER_LIST
FROM '/home/csmajs/ksuh004/Downloads/project/data/usr_list.csv'
WITH DELIMITER ';';
ALTER SEQUENCE user_list_list_id_seq RESTART 55906;

COPY USR
FROM '/home/csmajs/ksuh004/Downloads/project/data/usr.csv'
WITH DELIMITER ';';

COPY USER_LIST_CONTAINS
FROM '/home/csmajs/ksuh004/Downloads/project/data/usr_list_contains.csv'
WITH DELIMITER ';';

COPY CHAT
FROM '/home/csmajs/ksuh004/Downloads/project/data/chat.csv'
WITH DELIMITER ';';
ALTER SEQUENCE chat_chat_id_seq RESTART 5001;

COPY CHAT_LIST
FROM '/home/csmajs/ksuh004/Downloads/project/data/chat_list.csv'
WITH DELIMITER ';';

COPY MESSAGE
	(msg_id, 
	msg_text, 
	msg_timestamp, 
	sender_login,
	chat_id)
FROM '/home/csmajs/ksuh004/Downloads/project/data/message.csv'
WITH DELIMITER ';';
ALTER SEQUENCE message_msg_id_seq RESTART 50000;

COPY MEDIA_ATTACHMENT
FROM '/home/csmajs/ksuh004/Downloads/project/data/media_attachment.csv'
WITH DELIMITER ';';
ALTER SEQUENCE media_attachment_media_id_seq RESTART 2000;

COPY NOTIFICATION
FROM '/home/csmajs/ksuh004/Downloads/project/data/notification.csv'
WITH DELIMITER ';';
