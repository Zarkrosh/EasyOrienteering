export class Usuario {
    id: number;
    username: string;
    email: string;
    fecharegistro: Date;
}

export class Control {
    // Tipos de puntos
    public static readonly START = 0;
    public static readonly CONTROL = 1;
    public static readonly FINISH = 2;

    codigo: string;
    tipo: number;
    coords: Coordenadas;

    constructor(tipo, coords) {
        this.codigo = null;
        this.tipo = tipo;
        this.coords = coords;
    }
}

export class Recorrido {
    nombre: string;
    idControles: string[];

    constructor(nombre: string) {
        this.nombre = nombre;
        this.idControles = []
    }
}

export class Coordenadas {
    x: number;
    y: number;

    constructor(x: number, y: number) {
        this.x = x;
        this.y = y;
    }
}

export class Pair<T, U> {
    key: T;
    value: U;

    constructor(k: T, v: U) {
        this.key = k;
        this.value = v;
    }
}