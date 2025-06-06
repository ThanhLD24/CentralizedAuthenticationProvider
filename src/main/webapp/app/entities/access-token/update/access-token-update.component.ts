import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TokenStatus } from 'app/entities/enumerations/token-status.model';
import { IAccessToken } from '../access-token.model';
import { AccessTokenService } from '../service/access-token.service';
import { AccessTokenFormGroup, AccessTokenFormService } from './access-token-form.service';

@Component({
  selector: 'jhi-access-token-update',
  templateUrl: './access-token-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AccessTokenUpdateComponent implements OnInit {
  isSaving = false;
  accessToken: IAccessToken | null = null;
  tokenStatusValues = Object.keys(TokenStatus);

  protected accessTokenService = inject(AccessTokenService);
  protected accessTokenFormService = inject(AccessTokenFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AccessTokenFormGroup = this.accessTokenFormService.createAccessTokenFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ accessToken }) => {
      this.accessToken = accessToken;
      if (accessToken) {
        this.updateForm(accessToken);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const accessToken = this.accessTokenFormService.getAccessToken(this.editForm);
    if (accessToken.id !== null) {
      this.subscribeToSaveResponse(this.accessTokenService.update(accessToken));
    } else {
      this.subscribeToSaveResponse(this.accessTokenService.create(accessToken));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAccessToken>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(accessToken: IAccessToken): void {
    this.accessToken = accessToken;
    this.accessTokenFormService.resetForm(this.editForm, accessToken);
  }
}
