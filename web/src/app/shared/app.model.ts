export class AppSettings {
    public static readonly LOCAL_STORAGE_CARRERA = "carrera";
    public static readonly CANVAS_MAPAS_RESOLUCION: number = 0.8;
    public static readonly SEPARADOR_QR = "|";
    public static readonly TAM_LADO_QR = 200;
    public static readonly MARCA_AGUA_CONTROLES = "easyorienteering.com";
    public static readonly NOMBRE_RECORRIDO_SCORE = "Controles";
    public static readonly NUMERO_RESULTADOS_BUSQUEDA = 20;
}

export class Usuario {
    id: number;
    nombre: string;
    club: string;
    email: string;
    fecharegistro: Date;
}

export class Carrera {
    // Tipos de carrera
    public static readonly TIPO_EVENTO = "EVENTO"; 
    public static readonly TIPO_CIRCUITO = "CIRCUITO"; 
    // Modalidades
    public static readonly MOD_TRAZADO = "TRAZADO"; 
    public static readonly MOD_SCORE = "SCORE"; 
    
    id: number;
    nombre: string;
    organizador: Usuario;
    recorridos: Recorrido[];
    controles: Map<any, any>;
    tipo: string;
    modalidad: string;
    latitud: number;
    longitud: number;
    privada: boolean;
    notas: string;
    fecha: Date;
    creada: boolean; // True si la carrera existe en el sistema
                     // False cuando se está creando

    constructor() {
        this.nombre = this.tipo = this.modalidad = this.notas = "";
        this.recorridos = [];
        this.controles = new Map();
        this.creada = this.privada = false;
        this.id = this.organizador = this.latitud = this.longitud = this.fecha = null;
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
    puntuacion: number;

    constructor(codigo: string, tipo: string, coords: Coordenadas) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.coords = coords;
        this.puntuacion = 0;
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