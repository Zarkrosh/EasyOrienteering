
export class Utils {
    static getMensajeError(err: any, prefijo: string): string {
        let res = prefijo;
        let puntos = prefijo && prefijo.length > 0;

        if(err.status == 504) {
            res = "No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.";
        } else if(err.status == 404) {
            if(puntos) res += ": no existe";
            else res = "No existe";
        } else if(err.status == 403) {
            if(puntos) res += ": no tienes permiso";
            else res = "No tienes permiso";
        } else if(err.status == 401) {
            if(puntos) res += ": no estás autenticado";  
            else res = "No estás autenticado";
        } else {
            if(typeof err.error === 'string') {
                if(puntos) res += ": " + err.error;
                else res = err.error;
            } else if(typeof err.error) {
                if(typeof err.error.message === 'string') {
                    if(puntos) res += ": " + err.error.message;
                    else res = err.error.message;
                } else {
                    res += (puntos) ? ":" : "" + " Error desconocido";
                }
            } else {
                res += (puntos) ? ":" : "" + " Error desconocido";
            }
            console.log("[!] Debug error HTTP con código " + err.status);
            console.log(err);
        }

        return res;
    }
}