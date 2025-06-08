import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { TokenHistoryService } from '../service/token-history.service';
import { ITokenHistory } from '../token-history.model';
import { TokenHistoryFormService } from './token-history-form.service';

import { TokenHistoryUpdateComponent } from './token-history-update.component';

describe('TokenHistory Management Update Component', () => {
  let comp: TokenHistoryUpdateComponent;
  let fixture: ComponentFixture<TokenHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tokenHistoryFormService: TokenHistoryFormService;
  let tokenHistoryService: TokenHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TokenHistoryUpdateComponent],
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
      .overrideTemplate(TokenHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TokenHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tokenHistoryFormService = TestBed.inject(TokenHistoryFormService);
    tokenHistoryService = TestBed.inject(TokenHistoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const tokenHistory: ITokenHistory = { id: 12938 };

      activatedRoute.data = of({ tokenHistory });
      comp.ngOnInit();

      expect(comp.tokenHistory).toEqual(tokenHistory);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITokenHistory>>();
      const tokenHistory = { id: 6766 };
      jest.spyOn(tokenHistoryFormService, 'getTokenHistory').mockReturnValue(tokenHistory);
      jest.spyOn(tokenHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tokenHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tokenHistory }));
      saveSubject.complete();

      // THEN
      expect(tokenHistoryFormService.getTokenHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tokenHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(tokenHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITokenHistory>>();
      const tokenHistory = { id: 6766 };
      jest.spyOn(tokenHistoryFormService, 'getTokenHistory').mockReturnValue({ id: null });
      jest.spyOn(tokenHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tokenHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tokenHistory }));
      saveSubject.complete();

      // THEN
      expect(tokenHistoryFormService.getTokenHistory).toHaveBeenCalled();
      expect(tokenHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITokenHistory>>();
      const tokenHistory = { id: 6766 };
      jest.spyOn(tokenHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tokenHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tokenHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
