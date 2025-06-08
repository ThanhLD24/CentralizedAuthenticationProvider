import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITokenHistory } from '../token-history.model';
import { TokenHistoryService } from '../service/token-history.service';

@Component({
  templateUrl: './token-history-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TokenHistoryDeleteDialogComponent {
  tokenHistory?: ITokenHistory;

  protected tokenHistoryService = inject(TokenHistoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tokenHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
