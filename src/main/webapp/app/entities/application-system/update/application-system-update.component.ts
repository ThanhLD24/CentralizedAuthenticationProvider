import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IApplicationSystem } from '../application-system.model';
import { ApplicationSystemService } from '../service/application-system.service';
import { ApplicationSystemFormGroup, ApplicationSystemFormService } from './application-system-form.service';

@Component({
  selector: 'jhi-application-system-update',
  templateUrl: './application-system-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ApplicationSystemUpdateComponent implements OnInit {
  isSaving = false;
  applicationSystem: IApplicationSystem | null = null;

  usersSharedCollection: IUser[] = [];

  protected applicationSystemService = inject(ApplicationSystemService);
  protected applicationSystemFormService = inject(ApplicationSystemFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ApplicationSystemFormGroup = this.applicationSystemFormService.createApplicationSystemFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ applicationSystem }) => {
      this.applicationSystem = applicationSystem;
      if (applicationSystem) {
        this.updateForm(applicationSystem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const applicationSystem = this.applicationSystemFormService.getApplicationSystem(this.editForm);
    if (applicationSystem.id !== null) {
      this.subscribeToSaveResponse(this.applicationSystemService.update(applicationSystem));
    } else {
      this.subscribeToSaveResponse(this.applicationSystemService.create(applicationSystem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApplicationSystem>>): void {
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

  protected updateForm(applicationSystem: IApplicationSystem): void {
    this.applicationSystem = applicationSystem;
    this.applicationSystemFormService.resetForm(this.editForm, applicationSystem);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      ...(applicationSystem.users ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, ...(this.applicationSystem?.users ?? []))))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
