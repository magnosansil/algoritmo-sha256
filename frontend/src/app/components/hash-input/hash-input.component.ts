import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Output } from "@angular/core";
import { FormsModule } from "@angular/forms";

@Component({
  selector: "app-hash-input",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./hash-input.component.html",
  styleUrls: ["./hash-input.component.css"],
})
export class HashInputComponent {
  @Output() readonly submitted = new EventEmitter<string>();
  @Output() readonly cleared = new EventEmitter<void>();

  message = "";
  readonly examples = [
    "abc",
    "hello world",
    "olá, SHA-256!",
    "Uma mensagem longa para estudar o algoritmo.",
  ];

  submit(): void {
    this.submitted.emit(this.message);
  }

  clear(): void {
    this.message = "";
    this.cleared.emit();
  }

  useExample(example: string): void {
    this.message = example;
    this.submit();
  }
}
