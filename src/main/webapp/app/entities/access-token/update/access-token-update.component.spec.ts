import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { AccessTokenService } from '../service/access-token.service';
import { IAccessToken } from '../access-token.model';
import { AccessTokenFormService } from './access-token-form.service';

import { AccessTokenUpdateComponent } from './access-token-update.component';

describe('AccessToken Management Update Component', () => {
  let comp: AccessTokenUpdateComponent;
  let fixture: ComponentFixture<AccessTokenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let accessTokenFormService: AccessTokenFormService;
  let accessTokenService: AccessTokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AccessTokenUpdateComponent],
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
      .overrideTemplate(AccessTokenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AccessTokenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    accessTokenFormService = TestBed.inject(AccessTokenFormService);
    accessTokenService = TestBed.inject(AccessTokenService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const accessToken: IAccessToken = { id: 15796 };

      activatedRoute.data = of({ accessToken });
      comp.ngOnInit();

      expect(comp.accessToken).toEqual(accessToken);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccessToken>>();
      const accessToken = { id: 16439 };
      jest.spyOn(accessTokenFormService, 'getAccessToken').mockReturnValue(accessToken);
      jest.spyOn(accessTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accessToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: accessToken }));
      saveSubject.complete();

      // THEN
      expect(accessTokenFormService.getAccessToken).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(accessTokenService.update).toHaveBeenCalledWith(expect.objectContaining(accessToken));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccessToken>>();
      const accessToken = { id: 16439 };
      jest.spyOn(accessTokenFormService, 'getAccessToken').mockReturnValue({ id: null });
      jest.spyOn(accessTokenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accessToken: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: accessToken }));
      saveSubject.complete();

      // THEN
      expect(accessTokenFormService.getAccessToken).toHaveBeenCalled();
      expect(accessTokenService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccessToken>>();
      const accessToken = { id: 16439 };
      jest.spyOn(accessTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accessToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(accessTokenService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
