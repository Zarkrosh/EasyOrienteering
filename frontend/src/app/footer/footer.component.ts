import { Component, OnInit } from '@angular/core';
import { FooterService } from '../_services/footer.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  constructor(public footerService: FooterService) { }

  ngOnInit() {
  }

}
