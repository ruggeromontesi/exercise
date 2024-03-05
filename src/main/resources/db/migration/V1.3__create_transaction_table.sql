DROP TABLE IF EXISTS transaction CASCADE;
DROP SEQUENCE IF EXISTS transaction_seq;
CREATE SEQUENCE transaction_seq START WITH 1 INCREMENT BY 50;
create table transaction
(
    amount     float(53),
    status     tinyint check (status between 0 and 1),
    type       tinyint check (type between 0 and 1),
    account_id bigint,
    created    timestamp(6),
    id         bigint not null,
    primary key (id),
    CONSTRAINT FK_TRANSACTION_ACCOUNT FOREIGN KEY (account_id) REFERENCES account(id)

);