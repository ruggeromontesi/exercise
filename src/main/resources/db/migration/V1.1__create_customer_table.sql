DROP TABLE IF EXISTS customer CASCADE;
DROP SEQUENCE IF EXISTS  customer_seq;
CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 50;
create table customer
(
    id       bigint not null,
    username varchar(255),
    primary key (id)
);
insert into customer (username,id) values ('ruggero',1);