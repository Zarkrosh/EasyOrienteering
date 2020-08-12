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
    public static readonly TIPO_EVENTO = "EVENTO"; 
    public static readonly TIPO_CIRCUITO = "CIRCUITO"; 
    // Modalidades
    public static readonly MOD_TRAZADO = "LINEA"; 
    public static readonly MOD_SCORE = "SCORE"; 
    
    id: number;
    nombre: string;
    recorridos: Recorrido[];
    controles: Map<any, any>;
    tipo: string;
    modalidad: string;
    privada: boolean;
    creada: boolean; // True si la carrera existe en el sistema
                     // False cuando se está creando
    
    constructor(nombre: string, recorridos: Recorrido[], controles: Map<any, any>,
                tipo: string, modalidad: string, privada: boolean, creada: boolean) {
        this.nombre = nombre;
        this.recorridos = recorridos;
        this.controles = controles;
        this.tipo = tipo;
        this.modalidad = modalidad;
        this.privada = privada;
        this.creada = creada;
    }

}

export class Control {
    // Tipos de puntos
    public static readonly TIPO_SALIDA = "SALIDA";
    public static readonly TIPO_CONTROL = "CONTROL";
    public static readonly TIPO_META = "META";

    codigo: string;
    tipo: string;
    coords: Coordenadas;

    constructor(codigo: string, tipo: string, coords: Coordenadas) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.coords = coords;
    }
}

export class Recorrido {
    id: Number;
    nombre: string;
    trazado: string[];
    mapa: string; // Mapa en B64 "data:image/jpeg;base64, ..."

    constructor(nombre: string) {
        this.nombre = nombre;
        this.trazado = []
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