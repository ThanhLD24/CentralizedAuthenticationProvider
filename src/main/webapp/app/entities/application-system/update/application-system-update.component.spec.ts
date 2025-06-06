import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ApplicationSystemService } from '../service/application-system.service';
import { IApplicationSystem } from '../application-system.model';
import { ApplicationSystemFormService } from './application-system-form.service';

import { ApplicationSystemUpdateComponent } from './application-system-update.component';

describe('ApplicationSystem Management Update Component', () => {
  let comp: ApplicationSystemUpdateComponent;
  let fixture: ComponentFixture<ApplicationSystemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let applicationSystemFormService: ApplicationSystemFormService;
  let applicationSystemService: ApplicationSystemService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ApplicationSystemUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ApplicationSystemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ApplicationSystemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    applicationSystemFormService = TestBed.inject(ApplicationSystemFormService);
    applicationSystemService = TestBed.inject(ApplicationSystemService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const applicationSystem: IApplicationSystem = { id: 7708 };
      const users: IUser[] = [{ id: 3944 }];
      applicationSystem.users = users;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [...users];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ applicationSystem });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const applicationSystem: IApplicationSystem = { id: 7708 };
      const user: IUser = { id: 3944 };
      applicationSystem.users = [user];

      activatedRoute.data = of({ applicationSystem });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.applicationSystem).toEqual(applicationSystem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationSystem>>();
      const applicationSystem = { id: 13520 };
      jest.spyOn(applicationSystemFormService, 'getApplicationSystem').mockReturnValue(applicationSystem);
      jest.spyOn(applicationSystemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationSystem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicationSystem }));
      saveSubject.complete();

      // THEN
      expect(applicationSystemFormService.getApplicationSystem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(applicationSystemService.update).toHaveBeenCalledWith(expect.objectContaining(applicationSystem));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationSystem>>();
      const applicationSystem = { id: 13520 };
      jest.spyOn(applicationSystemFormService, 'getApplicationSystem').mockReturnValue({ id: null });
      jest.spyOn(applicationSystemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationSystem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicationSystem }));
      saveSubject.complete();

      // THEN
      expect(applicationSystemFormService.getApplicationSystem).toHaveBeenCalled();
      expect(applicationSystemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationSystem>>();
      const applicationSystem = { id: 13520 };
      jest.spyOn(applicationSystemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationSystem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(applicationSystemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
