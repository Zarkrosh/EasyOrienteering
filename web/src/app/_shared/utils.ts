
export class Utils {
    static getMensajeError(err: any, prefijo: string): string {
        let res = prefijo;

        if(err.status == 504) {
            res = "No hay conexión con el servidor. Espera un momento y vuelve a intentarlo.";
        } else if(err.status == 404) {
            res += ": no existe";
        } else if(err.status == 403) {
            res += ": no tienes permiso";
        } else if(err.status == 401) {
            res += ": no estás autenticado";  
        } else {
            if(typeof err.error === 'string') res += ": " + err.error;
            console.log("[!] Debug error HTTP con código " + err.status);
            console.log(err);
        }

        return res;
    }
}