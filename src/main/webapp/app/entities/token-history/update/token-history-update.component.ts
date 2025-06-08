import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TokenStatus } from 'app/entities/enumerations/token-status.model';
import { ITokenHistory } from '../token-history.model';
import { TokenHistoryService } from '../service/token-history.service';
import { TokenHistoryFormGroup, TokenHistoryFormService } from './token-history-form.service';

@Component({
  selector: 'jhi-token-history-update',
  templateUrl: './token-history-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TokenHistoryUpdateComponent implements OnInit {
  isSaving = false;
  tokenHistory: ITokenHistory | null = null;
  tokenStatusValues = Object.keys(TokenStatus);

  protected tokenHistoryService = inject(TokenHistoryService);
  protected tokenHistoryFormService = inject(TokenHistoryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TokenHistoryFormGroup = this.tokenHistoryFormService.createTokenHistoryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tokenHistory }) => {
      this.tokenHistory = tokenHistory;
      if (tokenHistory) {
        this.updateForm(tokenHistory);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tokenHistory = this.tokenHistoryFormService.getTokenHistory(this.editForm);
    if (tokenHistory.id !== null) {
      this.subscribeToSaveResponse(this.tokenHistoryService.update(tokenHistory));
    } else {
      this.subscribeToSaveResponse(this.tokenHistoryService.create(tokenHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITokenHistory>>): void {
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

  protected updateForm(tokenHistory: ITokenHistory): void {
    this.tokenHistory = tokenHistory;
    this.tokenHistoryFormService.resetForm(this.editForm, tokenHistory);
  }
}
