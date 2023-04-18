INSERT INTO persona_real(id_persona, nombre, apellido, ciudad) VALUES(1, 'Peter', 'Parker', 'Nueva York');
INSERT INTO persona_real(id_persona, nombre, apellido, ciudad) VALUES(2, 'Tony', 'Stark', 'Nueva York');
INSERT INTO persona_real(id_persona, nombre, apellido, ciudad) VALUES(3, 'Steven', 'Rogers', 'Nueva York');
INSERT INTO persona_real(id_persona, nombre, apellido, ciudad) VALUES(4, 'Thor', 'Odinson', 'Nueva York');
INSERT INTO persona_real(id_persona, nombre, apellido, ciudad) VALUES(5, 'Bruce', 'Wayne', 'Gotham');
INSERT INTO persona_real(id_persona, nombre, apellido, ciudad) VALUES(6, 'Clark', 'Kent', 'Nueva York');


INSERT INTO superheroe(id_sup, nombre, id_persona) VALUES(1, 'Batman', 5);
INSERT INTO superheroe(id_sup, nombre, id_persona) VALUES(2, 'Thor', 4);
INSERT INTO superheroe(id_sup, nombre, id_persona) VALUES(3, 'Capitan America', 3);
INSERT INTO superheroe(id_sup, nombre, id_persona) VALUES(4, 'Iron Man', 2);
INSERT INTO superheroe(id_sup, nombre, id_persona) VALUES(6, 'Superman', 6);

INSERT INTO villano(id_villano, nombre) VALUES(1, 'Thanos');
INSERT INTO villano(id_villano, nombre) VALUES(2, 'El Joker');
INSERT INTO villano(id_villano, nombre) VALUES(3, 'Bucky');
INSERT INTO villano(id_villano, nombre) VALUES(4, 'Loki');
INSERT INTO villano(id_villano, nombre) VALUES(5, 'El Duende Verde');
INSERT INTO villano(id_villano, nombre) VALUES(6, 'Lex Luthor');

INSERT INTO pelicula(id_pelicula, titulo, fecha_estreno) VALUES(1, 'Avengers: Endgame', '2019-04-22');
INSERT INTO pelicula(id_pelicula, titulo, fecha_estreno) VALUES(2, 'Spider-Man: No Way Home', '2021-12-13');
INSERT INTO pelicula(id_pelicula, titulo, fecha_estreno) VALUES(3, 'Batman: The Killing Joke', '2016-07-25');
INSERT INTO pelicula(id_pelicula, titulo, fecha_estreno) VALUES(4, 'Capitan America: Civil War', '2016-04-12');
INSERT INTO pelicula(id_pelicula, titulo, fecha_estreno) VALUES(5, 'Thor: Ragnarok', '2017-10-10');
INSERT INTO pelicula(id_pelicula, titulo, fecha_estreno) VALUES(6, 'Superman: la pelicula', '1979-02-08');
INSERT INTO pelicula(id_pelicula, titulo, fecha_estreno) VALUES(7, 'Superman II', '1981-06-19');

INSERT INTO protagoniza VALUES (6, 6, 6);
INSERT INTO protagoniza VALUES (6, 6, 7);

INSERT INTO escena VALUES(6, 1, 'Superman Escena 1', 20);
INSERT INTO escena VALUES(6, 2, 'Superman Escena 2', 7);
INSERT INTO escena VALUES(6, 3, 'Superman Escena b2', 16);
INSERT INTO escena VALUES(6, 4, 'Superman Escena 3', 35);
INSERT INTO escena VALUES(6, 5, 'Superman Escena 4', 24);
INSERT INTO escena VALUES(7, 1, 'Superman II E1', 46);
INSERT INTO escena VALUES(7, 2, 'Superman II E2', 38);
INSERT INTO escena VALUES(7, 3, 'Superman II E3', 41);
