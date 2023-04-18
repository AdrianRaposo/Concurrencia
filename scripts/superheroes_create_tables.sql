CREATE TABLE IF NOT EXISTS persona_real (
  id_persona INT,
  nombre VARCHAR(100),
  apellido VARCHAR(100),
  ciudad VARCHAR(100),
  PRIMARY KEY (id_persona)
);

CREATE TABLE IF NOT EXISTS superheroe (
  id_sup INT,
  nombre VARCHAR(100),
  avatar LONGBLOB,
  id_persona INT NOT NULL,
  PRIMARY KEY (id_sup),
  FOREIGN KEY (id_persona) REFERENCES persona_real (id_persona)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS villano (
  id_villano INT,
  nombre VARCHAR(100),
  PRIMARY KEY (id_villano)
);

CREATE TABLE IF NOT EXISTS pelicula (
  id_pelicula INT,
  titulo VARCHAR(100),
  fecha_estreno DATE,
  PRIMARY KEY (id_pelicula)
);

CREATE TABLE IF NOT EXISTS protagoniza (
  id_sup INT,
  id_villano INT,
  id_pelicula INT,
  PRIMARY KEY (id_sup, id_villano, id_pelicula),
  FOREIGN KEY (id_sup) REFERENCES superheroe (id_sup)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_villano) REFERENCES villano (id_villano)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_pelicula) REFERENCES pelicula (id_pelicula)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS escena (
  id_pelicula INT,
  n_orden INT,
  titulo VARCHAR(100),
  duracion INT,
  PRIMARY KEY (id_pelicula, n_orden),
  FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS rival (
  id_sup INT,
  id_villano INT,
  fecha_primer_encuentro DATE,
  PRIMARY KEY (id_sup, id_villano),
  FOREIGN KEY (id_sup) REFERENCES superheroe(id_sup)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_villano) REFERENCES villano(id_villano)
    ON DELETE CASCADE ON UPDATE CASCADE
);
