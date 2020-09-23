import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'fechaBonita'})
export class PipeFecha implements PipeTransform {

    readonly DELIMITADORES = ["/", "-"];
    readonly MESES = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"];

    transform(value: string): string {
        // Ejemplo de entrada: 2020-09-03 o 2020/09/03
        let res = value; // Si hay error devuelve lo mismo
        
        let i = 0;
        while(res === value && i < this.DELIMITADORES.length) {
            if(value.indexOf(this.DELIMITADORES[i]) > 0) {
                // Se detecta delimitador
                let campos = value.split(this.DELIMITADORES[i]);
                if(campos.length == 3) {
                    let dia = parseInt(campos[2]);
                    let mes = parseInt(campos[1]);
                    let anio = parseInt(campos[0]);

                    if(!isNaN(dia) && !isNaN(mes) && !isNaN(anio) &&
                        mes > 0 && mes <= this.MESES.length) {
                        
                        let sMes = this.MESES[mes-1];
                        res = dia + " de " + sMes + " de " + anio;
                    }
                }
            }
            i++;
        }
        
        return res;
    }
}