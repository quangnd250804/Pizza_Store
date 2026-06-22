import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';

@Component({
  selector: 'app-payment-result',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './payment-result.html',
})
export class PaymentResultComponent implements OnInit {
  status: 'success' | 'failed' = 'failed';

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const responseCode = params['vnp_ResponseCode'];
      if (responseCode === '00') {
        this.status = 'success';
      } else {
        this.status = 'failed';
      }
    });
  }
}
