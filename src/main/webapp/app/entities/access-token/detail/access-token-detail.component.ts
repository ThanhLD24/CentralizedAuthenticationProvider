import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IAccessToken } from '../access-token.model';

@Component({
  selector: 'jhi-access-token-detail',
  templateUrl: './access-token-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class AccessTokenDetailComponent {
  accessToken = input<IAccessToken | null>(null);

  previousState(): void {
    window.history.back();
  }
}
