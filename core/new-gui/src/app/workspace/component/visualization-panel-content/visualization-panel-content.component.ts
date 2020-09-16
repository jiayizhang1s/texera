import { Component, Inject, OnInit, AfterViewInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as c3 from 'c3';
import { PrimitiveArray } from 'c3';
import { List } from 'lodash';
import * as WordCloud from 'wordcloud';
import { ChartType, WordCloudTuple, DialogData } from '../../types/visualization.interface';

/**
 * VisualizationPanelContentComponent displays the chart based on the chart type and data in table.
 *
 * It will convert the table into data format required by c3.js.
 * Then it passes the data and figure type to c3.js for rendering the figure.
 * @author Mingji Han, Xiaozhen Liu
 */
@Component({
  selector: 'texera-visualization-panel-content',
  templateUrl: './visualization-panel-content.component.html',
  styleUrls: ['./visualization-panel-content.component.scss']
})
export class VisualizationPanelContentComponent implements OnInit, AfterViewInit {
  // this readonly variable must be the same as HTML element ID for visualization
  public static readonly CHART_ID = '#texera-result-chart-content';
  public static readonly WORD_CLOUD_ID = 'texera-word-cloud';
  public static readonly WIDTH = 1000;
  public static readonly HEIGHT = 800;
  private table: object[];
  // private columns: string[] = [];


  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.table = data.table;
  }

  ngOnInit() {
    // this.columns = Object.keys(this.table[0]).filter(x => x !== '_id');
  }

  ngAfterViewInit() {
    switch (this.data.chartType) {
      // correspond to TexeraWordCloud.java
      case ChartType.WORD_CLOUD: this.onClickGenerateWordCloud(); break;
      // correspond to BarChartSink.java
      case ChartType.BAR:
      case ChartType.STACKED_BAR:
      // correspond to TexeraPieChart.java
      case ChartType.PIE:
      case ChartType.DONUT:
      // correspond to TexeraLineChart.java
      case ChartType.LINE:
      case ChartType.SPLINE: this.onClickGenerateChart(); break;
    }
  }

  onClickGenerateWordCloud() {
    const dataToDisplay: object[] = [];
    // this.table.shift(); // In the old engine the first line is column names

    const wordCloudTuples: Array<WordCloudTuple> = new Array;
    this.table.forEach(element => {
      const tupleContent = (element as Array<object>);
      const wordCloudTuple: WordCloudTuple = {
        word: tupleContent[0] as unknown as string,
        count: tupleContent[1] as unknown as number,
      };
      wordCloudTuples.push(wordCloudTuple);
    });

    // const wordCloudTuples = this.table as ReadonlyArray<WordCloudTuple>; // Cannot successfuly cast for lack of schema
    for (const tuple of wordCloudTuples) {
      dataToDisplay.push([tuple.word, tuple.count]);
    }
    WordCloud(document.getElementById(VisualizationPanelContentComponent.WORD_CLOUD_ID) as HTMLElement,
           { list: dataToDisplay } );
  }

  onClickGenerateChart() {

    const dataToDisplay: Array<[string, ...PrimitiveArray]> = [];
    // const category: string[] = [];
    // for (let i = 1; i < this.columns?.length; i++) {
    //   category.push(this.columns[i]);
    // }

    // c3.js requires the first element in the data array is the data name.
    // the remaining items are data.

    // let firstRow = true;
    const columnCount = (this.table[0] as any as Array<object>).length;
    for (const row of this.table) {
      // if (firstRow) {
      //   firstRow = false;
      //   continue;
      // }
      const items: [string, ...PrimitiveArray] = [(row as any)[0]];
      for (let i = 1; i < columnCount; i++) {
        items.push(Number((row as any)[i]));
      }
      dataToDisplay.push(items);
    }

    c3.generate({
      size: {
        height: VisualizationPanelContentComponent.HEIGHT,
        width: VisualizationPanelContentComponent.WIDTH
      },
      data: {
        columns: dataToDisplay,
        type: this.data.chartType as c3.ChartType
      },
      // axis: {
      //   x: {
      //     type: 'category',
      //     categories: category
      //   }
      // },
      bindto: VisualizationPanelContentComponent.CHART_ID
    });
  }

}
