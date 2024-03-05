drop table if exists account cascade;
drop sequence if exists account_seq;
CREATE SEQUENCE account_seq START WITH 1 INCREMENT BY 50;
create table account
(
    id          bigint  not null,
    customer_id bigint,
    currency    tinyint not null check (currency between 0 and 0),
    type        tinyint check (type between 0 and 1),
    primary key (id),
    CONSTRAINT FK_ACCOUNT_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer(id)
);