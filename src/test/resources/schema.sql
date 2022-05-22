drop table if exists machine;
create table machine
(
    id            serial
        constraint machine_pk primary key,
    status        varchar(255),
    label         varchar(255),
    modified_date bigint
);