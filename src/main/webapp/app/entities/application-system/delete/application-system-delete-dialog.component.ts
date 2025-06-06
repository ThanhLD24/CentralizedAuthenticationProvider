import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IApplicationSystem } from '../application-system.model';
import { ApplicationSystemService } from '../service/application-system.service';

@Component({
  templateUrl: './application-system-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ApplicationSystemDeleteDialogComponent {
  applicationSystem?: IApplicationSystem;

  protected applicationSystemService = inject(ApplicationSystemService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.applicationSystemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
