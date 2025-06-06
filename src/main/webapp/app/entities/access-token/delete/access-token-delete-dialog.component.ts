import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAccessToken } from '../access-token.model';
import { AccessTokenService } from '../service/access-token.service';

@Component({
  templateUrl: './access-token-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AccessTokenDeleteDialogComponent {
  accessToken?: IAccessToken;

  protected accessTokenService = inject(AccessTokenService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.accessTokenService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
