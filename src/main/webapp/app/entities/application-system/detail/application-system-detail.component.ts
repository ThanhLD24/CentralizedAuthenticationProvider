import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IApplicationSystem } from '../application-system.model';

@Component({
  selector: 'jhi-application-system-detail',
  templateUrl: './application-system-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ApplicationSystemDetailComponent {
  applicationSystem = input<IApplicationSystem | null>(null);

  previousState(): void {
    window.history.back();
  }
}
