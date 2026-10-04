import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { HttpErrorResponse } from "@angular/common/http";
import { HashInputComponent } from "./components/hash-input/hash-input.component";
import { HashResultComponent } from "./components/hash-result/hash-result.component";
import { TraceStepComponent } from "./components/trace-step/trace-step.component";
import { Sha256ApiService } from "./services/sha256-api.service";
import { Sha256Trace } from "./models/trace.models";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [
    CommonModule,
    HashInputComponent,
    HashResultComponent,
    TraceStepComponent,
  ],
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent {
  trace: Sha256Trace | null = null;
  loading = false;
  error = "";

  constructor(private readonly api: Sha256ApiService) {}

  calculate(message: string): void {
    this.loading = true;
    this.error = "";
    this.api.calculate(message).subscribe({
      next: (trace) => {
        this.trace = trace;
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        this.error =
          error.error?.error ??
          "Não foi possível conectar à API Java. Verifique se o backend está em execução.";
      },
    });
  }

  clear(): void {
    this.trace = null;
    this.error = "";
  }
}
