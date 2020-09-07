import { Injectable } from '@angular/core';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class NavbarService {
  visible: boolean;
  loggedInView: boolean;

  constructor(private tokenService: TokenStorageService) { 
    this.show();
    this.setLoggedInView(tokenService.isLoggedIn());
  }

  hide() { this.visible = false; }
  show() { this.visible = true; }
  setLoggedInView(set: boolean) { this.loggedInView = set };
}
