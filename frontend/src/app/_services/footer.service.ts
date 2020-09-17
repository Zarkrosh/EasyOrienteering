import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FooterService {
  visible: boolean;

  constructor() { this.show() }

  hide() { this.visible = false; }
  show() { this.visible = true; }
}
