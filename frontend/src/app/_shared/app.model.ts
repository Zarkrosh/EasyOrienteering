import { Timestamp } from 'rxjs/internal/operators/timestamp';

export class AppSettings {
    public static readonly LOCAL_STORAGE_CARRERA = "carrera";
    public static readonly CANVAS_MAPAS_RESOLUCION: number = 0.8;
    public static readonly SEPARADOR_QR = "|";
    public static readonly TAM_LADO_QR = 200;
    public static readonly MARCA_AGUA_CONTROLES = "easyorienteering.com";
    public static readonly NOMBRE_RECORRIDO_SCORE = "Controles";
    public static readonly NUMERO_RESULTADOS_BUSQUEDA = 20;
    public static readonly CODIGO_SALIDA = "SALIDA";
    public static readonly CODIGO_META = "META";
}

export class Usuario {
    id: number;
    nombre: string;
    email: string;
    club: string;
    fechaRegistro: string;
    roles: string[];
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
    fecha: string;
    soloRecorridos: boolean; // Interno

    constructor() {
        this.nombre = this.tipo = this.modalidad = this.notas = "";
        this.recorridos = [];
        this.controles = new Map();
        this.privada = false;
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
    id: number;
    nombre: string;
    trazado: string[]; // Sin incluir salida/meta
    mapa: any; // Mapa en B64 "data:image/jpeg;base64, ..." o true/false para indicar presencia

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


export class Registro {
    control: string;   // Codigo
    fecha: string;
}

export class Participacion {
    fechaInicio: string;
    registros: Registro[];
    abandonado: boolean;
    pendiente: boolean;
    corredor: Usuario;
}

export class ParticipacionesRecorridoResponse {
    modalidad: string;
    idCarrera: number;
    recorrido: Recorrido;
    puntuacionesControles: Map<string, number>;
    participaciones: Participacion[];
}

export class ResultadoUsuario {
    public static readonly TIPO_OK = "OK";
    public static readonly TIPO_PENDIENTE = "PENDIENTE";
    public static readonly TIPO_ABANDONADO = "ABANDONADO";
    public static readonly ORDEN_TIPOS = [ResultadoUsuario.TIPO_OK, ResultadoUsuario.TIPO_PENDIENTE, ResultadoUsuario.TIPO_ABANDONADO];

    idUsuario: number;
    posicion: number;
    nombre: string;
    club: string;
    tiempoTotal: number;
    diferenciaGanador: number;
    puntuacion: number;
    tipo: string;
    parciales: ParcialUsuario[];
    puntosRegistrados: boolean[];

    constructor(idUsuario: number, nombre: string, club: string, tiempoTotal: number, puntuacion: number, tipo: string) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.club = club;
        this.tiempoTotal = tiempoTotal;
        this.puntuacion = puntuacion;
        this.tipo = tipo;
    }
}

export class ParcialUsuario {
    tiempoParcial: number;
    tiempoAcumulado: number;
    posicionParcial: number;
    posicionAcumulada: number;

    constructor(tiempoParcial: number, tiempoAcumulado: number) {
        this.tiempoParcial = tiempoParcial;
        this.tiempoAcumulado = tiempoAcumulado;
    }
}


