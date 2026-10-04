import { TestBed } from "@angular/core/testing";
import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { Sha256ApiService } from "./sha256-api.service";

describe("Sha256ApiService", () => {
  let service: Sha256ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(Sha256ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it("envia a mensagem para a API Java", () => {
    service.calculate("abc").subscribe();
    const request = http.expectOne("http://localhost:8080/api/hash");
    expect(request.request.method).toBe("POST");
    expect(request.request.body).toEqual({ message: "abc" });
    request.flush({ hash: "ba7816bf..." });
  });

  it("propaga erros de comunicação", () => {
    let failed = false;
    service.calculate("abc").subscribe({ error: () => (failed = true) });
    http
      .expectOne("http://localhost:8080/api/hash")
      .error(new ProgressEvent("offline"));
    expect(failed).toBeTrue();
  });
});
