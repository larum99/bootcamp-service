CREATE TABLE IF NOT EXISTS bootcamp (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_lanzamiento DATE,
    duracion INT,
    UNIQUE(nombre)
);