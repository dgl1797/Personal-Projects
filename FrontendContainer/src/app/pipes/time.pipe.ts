import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  standalone: true,
  name: 'timepipe',
})
export class TimePipe implements PipeTransform {
  transform(timestampString: string): string {
    const timestamp = parseInt(timestampString);
    const today = new Date(Date.now());
    const sendingDate = new Date(timestamp);
    const errorString =
      'oops, looks like an unexpected behavior lead to a message from the future!';

    // years ago
    const yago = today.getFullYear() - sendingDate.getFullYear();
    //prettier-ignore
    if (yago < 0) throw new Error(errorString);
    if (yago > 0) return `${yago} years ago`;

    // months ago
    const mago = today.getMonth() - sendingDate.getMonth();
    if (mago < 0) throw new Error(errorString);
    if (mago > 0) return `${mago} months ago`;

    // days ago
    const dago = today.getDate() - sendingDate.getDate();
    if (dago < 0) throw new Error(errorString);
    if (dago > 0) return `${dago} days ago`;

    const hh = sendingDate.getHours();
    const mm = sendingDate.getMinutes();
    return `${hh}:${mm}`;
  }
}
