import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'tiempo'})
export class PipeTiempo implements PipeTransform {
    transform(value: number, prefix: string = ""): string {
        let res = prefix;
        if(value !== null) {
            let hh = Math.floor(value / 3600);
            value -= hh * 3600;
            let mm = Math.floor(value / 60);
            let ss = Math.floor(value - mm * 60);

            if(hh > 0) {
                // hh:mm:ss
                res += hh + ":";
                res += ((mm < 10) ? "0" : "") + mm;
            } else {
                // m:ss
                res += mm;
            }
            res += ((ss < 10) ? ":0" : ":") + ss;

            if(hh == 0 && mm == 0 && ss == 0) res = "";
        }
        
        return res;
    }
}