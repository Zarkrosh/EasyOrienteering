export class AppSettings {
    public static readonly LOCAL_STORAGE_CARRERA = "carrera";
}

export class Usuario {
    id: number;
    username: string;
    email: string;
    fecharegistro: Date;
}

export class Carrera {
    // Tipos de carrera
    public static readonly EVENTO = 0; 
    public static readonly CIRCUITO_PERMANENTE = 1; 
    
    nombre: string;
    recorridos: Recorrido[];
    controles: Control[];
    tipo: number;
    creada: boolean; // True si la carrera existe en el sistema
                     // False cuando se está creando
    
    constructor(nombre: string, recorridos: Recorrido[], controles: Control[],
                tipo: number, creada: boolean) {
        this.nombre = nombre;
        this.recorridos = recorridos;
        this.controles = controles;
        this.tipo = tipo;
        this.creada = creada;
    }

}

export class Control {
    // Tipos de puntos
    public static readonly SALIDA = 0;
    public static readonly CONTROL = 1;
    public static readonly META = 2;

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