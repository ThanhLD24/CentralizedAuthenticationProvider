import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITokenHistory } from '../token-history.model';

@Component({
  selector: 'jhi-token-history-detail',
  templateUrl: './token-history-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TokenHistoryDetailComponent {
  tokenHistory = input<ITokenHistory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
