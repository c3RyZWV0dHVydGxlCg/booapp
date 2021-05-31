insert into clinic(DESCRIPTION, NAME)
values ('Just a clinic', 'Princeton Plainsboro Clinic');

insert into time_slot(start, end)
values ('08:00:00', ' 08:15:00'),
       ('08:15:00', ' 08:30:00'),
       ('08:30:00', ' 08:45:00'),
       ('08:45:00', ' 09:00:00'),
       ('09:00:00', ' 09:15:00');

insert into patient(first_name, last_name)
values ('Dwight', 'Schrute'),
       ('Michael', 'Michael Scott'),
       ('Oscar', 'Martinez'),
       ('Kevin', 'Malone');

insert into provider(FIRST_NAME, LAST_NAME, OCCUPATION, CLINIC_ID)
values ('Gregory', 'House', 'diagnostician', 1),
       ('Lisa', 'Cuddy', 'neurologist', 1),
       ('James', 'Wilson', 'oncologist', 1),
       ('Eric', 'Foreman', 'neurologist', 1),
       ('Robert', 'Chase', 'surgeon', 1);


insert into appointment(DATE, PATIENT, PROVIDER, TIME_SLOT)
values ('2021-06-01', 1, 1, 1),
       ('2021-06-01', 2, 2, 2),
       ('2021-06-01', 3, 1, 3),
       ('2021-06-01', 4, 3, 3);
