import { ComponentFixture, TestBed } from "@angular/core/testing";
import { HashInputComponent } from "./hash-input.component";

describe("HashInputComponent", () => {
  let fixture: ComponentFixture<HashInputComponent>;
  let component: HashInputComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HashInputComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(HashInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("emite a mensagem enviada", () => {
    let value = "";
    component.submitted.subscribe((message) => (value = message));
    component.message = "abc";
    component.submit();
    expect(value).toBe("abc");
  });
});
