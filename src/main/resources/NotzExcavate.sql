create table if not exists excavatormodel(
    id integer primary key autoincrement,
    plotid varchar(36) unique not null,
    excavator blob not null
);
create table if not exists shovelmodel(
    id integer primary key autoincrement,
    name varchar(36) unique not null,
    shovel blob not null
);